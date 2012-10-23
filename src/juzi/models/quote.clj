(ns juzi.models.quote
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defrecord Quote [id wall-id quote-text up-votes down-votes created-at updated-at])

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
                        (Quote. quote-id wall-id quote-text up-votes down-votes (:created-at quote) (Date.))))))

(defn delete-quote! [quote-id]
  (let [quote (riak/find-quote quote-id)]
    (if quote
      (do  (riak/delete-quote! quote-id) true)
      false)))