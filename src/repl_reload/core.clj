(ns repl-reload.core
  (:require [clojure.tools.namespace.repl :as repl]
            [clojure.java.classpath :as classpath]
            [ns-tracker.core :as tracker]))

(defonce my-aliases (atom nil))

(defn restore-aliases []
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
                     (when (pos? (count (track)))
                       (reload)))))
      (.setDaemon true)
      (.start))))
