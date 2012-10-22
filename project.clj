(defproject juzi "0.1.0-SNAPSHOT"
  :description "Quote wall app!"
  :url "https://github.com/rtircher/juzi"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cheshire "4.0.3"]
                 [com.novemberain/welle "1.4.0-SNAPSHOT"]
                 [compojure "1.1.3"]
                 ;[clj-time "0.4.4"]
                 ]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler juzi.core/app
         :port 4000}
  :dev-dependencies [[ring-mock "0.1.2"]])
