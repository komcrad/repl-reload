(ns repl-cud.core
  (:require [clojure.tools.namespace.repl :as repl]
            [ns-tracker.core :as tracker]))

(defonce my-aliases (atom nil))

(defn reload []
  (try
    (reset! my-aliases (merge @my-aliases (ns-aliases *ns*)))
    (when (nil? (repl/refresh :after 'clojure.main/repl-prompt))
      (doseq [aliased @my-aliases]
        (let [sym (first aliased)
              target (second aliased)]
          (ns-unalias *ns* sym)
          (alias sym (symbol (.toString target))))))
  (catch Throwable e nil)))

(defn auto-reload []
  (let [track (tracker/ns-tracker ["./src" "./test"])
        my-ns *ns*]
    (doto
      (Thread.
        #(while true (binding [*ns* my-ns]
                       (when (pos? (count (track)))
                         (reload)
                         (println "...finished reloading")))
           (Thread/sleep 500)))
      (.setDaemon true)
      (.start))))
