(defproject komcrad/repl-reload "0.1.3-SNAPSHOT"
  :description "Reloads the repl on file change"
  :url "https://github.com/komcrad/repl-reload"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.namespace "1.0.0"]
                 [ns-tracker "0.4.0"]]
  :repl-options {:init-ns repl-reload.core})
