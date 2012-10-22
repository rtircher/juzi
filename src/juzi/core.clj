(ns juzi.core
  (:use compojure.core
        [juzi.response :only [json]]
        [juzi.models.wall :only [wall create-wall! update-wall!]]
        [juzi.models.quote :only [quotes create-quote! update-quote!]])
  (:require [compojure.handler :as handler]
            [compojure.route   :as route]
            juzi.middlewares.logger))


(defroutes api-routes
  (GET "/walls/:id" [id]
    (json (wall id)))
  (POST "/walls" []
    (json (create-wall!)))
  (PUT "/walls" []
    (json (update-wall!)))

  (GET "/walls/:wall-id/quotes" [wall-id]
    (json (quotes wall-id)))
  (POST "/walls/:wall-id/quotes" {params :params}
    (json (create-quote! params)))
  (PUT "/walls/:wall-id/quotes/:quote-id" [quote-id :as {params :params}]
    (json (update-quote! quote-id params)))

  (route/not-found "Not Found"))


(def app
  (-> (handler/api api-routes)
      (juzi.middlewares.logger/wrap-request-logging)))
