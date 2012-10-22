(ns juzi.models.quote
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defrecord Quote [wall-id quote-text up-votes down-votes created-at updated-at])

(defn quotes [wall-id]
  (map (fn [{:keys [wall-id quote-text up-votes down-votes created-at updated-at]}]
         (Quote. wall-id quote-text up-votes down-votes created-at updated-at))
       (riak/find-quotes wall-id)))

(defn create-quote! [{:keys [wall-id quote-text up-votes down-votes]}]
  (let [now (Date.)]
    (riak/store-quote! (Quote. wall-id quote-text up-votes down-votes now now))))

(defn update-quote! [quote-id {:keys [wall-id quote-text up-votes down-votes created-at]}]
  (riak/store-quote! quote-id
                     (Quote. wall-id quote-text up-votes down-votes created-at (Date.))))
