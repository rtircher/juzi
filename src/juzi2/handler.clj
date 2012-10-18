(ns juzi2.handler
  (:use compojure.core)
  (:require [compojure.handler       :as handler]
            [compojure.route         :as route]
            [juzi.middlewares.logger :as logger]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes)
  (logger/wrap-request-loging))
