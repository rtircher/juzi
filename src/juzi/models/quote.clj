(ns juzi.models.quote
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defn quotes [wall-id]
  {:quotes ["a quote" "another quote"]})

(defn create-quote! [wall-id quote-text]
  (riak/create-quote! {:wall-id 1
                       :text quote-text
                       :up-votes 0
                       :down-votes 0
                       :created-at (Date.)
                       :updated-at (Date.)
                       }))

(defn update-quote! [wall-id quote-id quote-text]
  {:update [quote-text]})
