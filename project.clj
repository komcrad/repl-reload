(defproject komcrad/repl-reload "0.1.0"
  :description "Reloads the repl on file change"
  :url "http://github.com/komcrad/repl-reload"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [ns-tracker "0.4.0"]]
  :repl-options {:init-ns repl-reload.core})
