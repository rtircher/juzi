(ns juzi.core
  (:use compojure.core
        [juzi.response :only [json]]
        [juzi.models.wall :only [wall create-wall! update-wall!]]
        [juzi.models.quote :only [quotes create-quote! update-quote! delete-quote!]]
        [slingshot.slingshot :only [try+]])
  (:require [compojure.handler :as handler]
            [compojure.route   :as route]
            juzi.middlewares.logger
            juzi.middlewares.url-rewrite))

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
  (DELETE "/walls/:wall-id/quotes/:quote-id" [wall-id quote-id]
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
      juzi.middlewares.url-rewrite/ignore-trailing-slash
      (juzi.middlewares.logger/wrap-request-logging)))
