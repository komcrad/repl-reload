(ns repl-reload.core
  (:require [clojure.tools.namespace.repl :as repl]
            [clojure.tools.namespace.dir :as dir]
            [clojure.tools.namespace.find :as find]
            [clojure.tools.namespace.file :as file]
            [clojure.java.classpath :as classpath]
            [ns-tracker.core :as tracker]))

(defonce my-aliases (atom nil))

(defn restore-aliases []
  (doseq [aliased @my-aliases]
    (let [sym (first aliased)
          target (second aliased)]
      (ns-unalias *ns* sym)
      (alias sym (symbol (.toString target))))))

(defn- get-files-of-ns [tracker target-ns-list]
  (let [target? (set target-ns-list)]
    (reduce-kv (fn [aux file ns]
                 (if (target? ns)
                   (conj aux
                         file)
                   aux))
               []
               (::file/filemap tracker))))

(defn- touch-files-of-ns-aux [tracker changed-ns-list]
  (let [files (get-files-of-ns tracker
                               changed-ns-list)]
    (if (seq files)
      (dir/scan-files tracker
                      files
                      {:platform find/clj
                       :add-all? true})
      tracker)))

(defn- touch-files-of-ns
  "Forcibly reload files corresponding `changed-ns`.
  It makes :ns-tracker/resource-deps work again.
  See https://github.com/weavejester/ns-tracker#declaring-dependencies-to-static-resources"
  [changed-ns-list]
  (alter-var-root #'repl/refresh-tracker
                  touch-files-of-ns-aux
                  changed-ns-list))

(defn reload []
  (try
    (reset! my-aliases (merge @my-aliases (ns-aliases *ns*)))
    (let [loaded (repl/refresh :after 'repl-reload.core/restore-aliases)]
      (if (nil? loaded)
        (do (printf "\n%s=> " (ns-name *ns*))
            (.flush *out*))
        (do (println loaded)
            (printf "\n%s\n%s=> "
                    loaded (ns-name *ns*))
            (.flush *out*))))
    (catch Throwable e (println e))))

(defn auto-reload []
  (let [track (tracker/ns-tracker
               (mapv str (classpath/classpath-directories)))
        my-ns *ns*
        my-out *out*]
    (doto
     (Thread.
      #(while true (binding [*ns* my-ns
                             *out* my-out]
                     (Thread/sleep 500)
                     (let [changed-ns (track)]
                       (when (seq changed-ns)
                         (touch-files-of-ns changed-ns)
                         (reload))))))
      (.setDaemon true)
      (.start))))
