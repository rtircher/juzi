(defproject juzi "0.1.0-SNAPSHOT"
  :description "Quote wall app!"
  :url "https://github.com/rtircher/juzi"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-json "0.5.0"]
                 [compojure "1.1.3"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler juzi.handler/app
         :port 4000}
  :dev-dependencies [[ring-mock "0.1.2"]])