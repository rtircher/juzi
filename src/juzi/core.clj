(ns juzi.core
  (:use compojure.core
        [juzi.response :only [json]]
        [juzi.models.wall :only [wall create-wall! update-wall! delete-wall!]]
        [juzi.models.quote :only [quotes create-quote! update-quote! delete-quote!]]
        [slingshot.slingshot :only [try+]])
  (:require [compojure.handler :as handler]
            [compojure.route   :as route]
            [middlewares logger
                         url-rewrite]))

(defroutes api-routes
  (GET "/walls/:id" [id]
    (json (wall id)))
  (POST "/walls" {params :params}
    (json (create-wall! params)))
  (PUT "/walls/:id" [id :as {params :params}]
    (json (update-wall! id params)))
  (DELETE "/walls/:id" [id]
    (json (delete-wall! id)))

  (GET "/walls/:wall-id/quotes" [wall-id]
    (json (quotes wall-id)))
  (POST "/walls/:wall-id/quotes" {params :params}
    (json (create-quote! params)))
  (PUT "/walls/:wall-id/quotes/:quote-id" [quote-id :as {params :params}]
    (json (update-quote! quote-id params)))
  (DELETE "/walls/:wall-id/quotes/:quote-id" [quote-id]
    (json (delete-quote! quote-id)))

  (route/not-found "Not Found"))


(defn- wrap-error-handling [handler]
  (fn [req]
    (try+
      (handler req)
      (catch [:type :not-found] {:keys [message]}
        (json {:error message} 404))
      (catch [:type :invalid] {:keys [message]}
        (json {:error message} 400)))))

(def app
  (-> (handler/api api-routes)
      wrap-error-handling
      middlewares.url-rewrite/ignore-trailing-slash
      (middlewares.logger/wrap-request-logging)))
