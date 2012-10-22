(ns juzi.models.quote
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defn quotes [wall-id]
  {:quotes ["a quote" "another quote"]})

(defn create-quote! [wall-id quote-text]
  (let [now (Date.)]
    (riak/store-quote! {:wall-id wall-id
                        :text quote-text
                        :up-votes 0
                        :down-votes 0
                        :created-at now
                        :updated-at now
                        })))

(defn update-quote! [wall-id quote-id {:keys [quote-text up-votes down-votes]}]
  (riak/store-quote! quote-id
                     {:wall-id wall-id
                      :text quote-text
                      :up-votes up-votes
                      :down-votes down-votes
                      :updated-at (Date.)
                      }))
