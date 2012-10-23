(ns juzi.models.quote
  (:use [slingshot.slingshot :only [throw+]])
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defrecord Quote [id wall-id quote-text up-votes down-votes created-at updated-at])

(defn- quote-not-found [quote-id]
  (throw+ {:type    :not-found
           :message (str "quote " quote-id " not found")}))

(defn quotes [wall-id]
  (map map->Quote (riak/find-quotes wall-id)))

(defn create-quote! [{:keys [wall-id quote-text up-votes down-votes]}]
  (let [now (Date.)]
    (map->Quote
     (riak/store-quote! (Quote. nil wall-id quote-text up-votes down-votes now now)))))

(defn update-quote! [quote-id {:keys [wall-id quote-text up-votes down-votes]}]
  (if-let [quote (riak/find-quote quote-id)]
    (map->Quote
     (riak/store-quote! quote-id
                        (Quote. quote-id wall-id quote-text up-votes down-votes (:created-at quote) (Date.))))
    (quote-not-found quote-id)))

(defn delete-quote! [quote-id]
  (if-let [quote (riak/find-quote quote-id)]
    (do  (riak/delete-quote! quote-id)
         quote)
    (quote-not-found quote-id)))
