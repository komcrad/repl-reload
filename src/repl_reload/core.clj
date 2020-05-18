(ns repl-reload.core
  (:require [clojure.tools.namespace.repl :as repl]
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
    (repl/refresh :after 'repl-reload.core/restore-aliases)
  (catch Throwable e (println e))))

(defn auto-reload []
  (let [track (tracker/ns-tracker
                (mapv str (clojure.java.classpath/classpath-directories)))
        my-ns *ns*
        my-out *out*]
    (doto
      (Thread.
        #(while true (binding [*ns* my-ns
                               *out* my-out]
                       (Thread/sleep 500)
                       (when (pos? (count (track)))
                         (when (nil? (reload))
                           (printf "\n%s=> " (ns-name *ns*))
                           (.flush *out*))))))
      (.setDaemon true)
      (.start))))
