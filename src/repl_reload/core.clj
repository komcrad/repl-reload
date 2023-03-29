(ns repl-reload.core
  (:require [clojure.tools.namespace.repl :as repl]
            [clojure.string :as strs]
            [ns-tracker.core :as tracker]
            [clojure.java.classpath])
  (:import [java.util.regex Pattern]))

(defonce my-aliases (atom nil))

(defn are-you-there-mount []
  (boolean
   (try
     (require '[mount.core])
     (resolve 'mount.core/start)
     (catch Exception e))))

(defn restore-aliases []
  (when (are-you-there-mount)
    (eval
     '(mount.core/start)))
  (doseq [aliased @my-aliases]
    (let [sym (first aliased)
          target (second aliased)]
      (ns-unalias *ns* sym)
      (alias sym (symbol (.toString target))))))

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

(defn get-tracked-dirs [coll]
  (let [paths (mapv str (clojure.java.classpath/classpath-directories))
        pattern (re-pattern (strs/join "|" (map #(Pattern/quote %) coll)))]
    (filter #(not (re-find pattern %)) paths)))

(defn auto-reload [& args]
  (let [track (tracker/ns-tracker (get-tracked-dirs args))
        my-ns *ns*
        my-out *out*]
    (doto
     (Thread.
      #(while true (binding [*ns* my-ns
                             *out* my-out]
                     (Thread/sleep 500)
                     (when (pos? (count (track)))
                       (when (are-you-there-mount)
                         (eval '(mount.core/stop)))
                       (reload)))))
      (.setDaemon true)
      (.start))))
