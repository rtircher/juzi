(ns juzi.riak
  (:use juzi.config)
  (:require [clojurewerkz.welle.core    :as wc]
            [clojurewerkz.welle.kv      :as kv]
            [clojurewerkz.welle.mr      :as mr])
  ;(:import com.basho.riak.client.http.util.Constants)
  )

(def ^:private db-connected (ref false))
(def ^:private quotes-bucket "quotes")

(defn- ensure-db-connected []
  (dosync
   (when (not @db-connected)
     (wc/connect! (:db-url config))
     (ref-set db-connected true))))

(defn- max-id-for [bucket]
  (ensure-db-connected)
  (read-string
   (first
    (mr/map-reduce
     {:inputs bucket,
      :query [{:map {:language "javascript",
                     :source   "function(value) { return [value.key]}"}},
              {:reduce {:language "javascript",
                        :source   "
function(valueList) {
  return [ valueList.reduce(
    function(acc, value){
      return (+acc > +value) ? acc : value;
    }
  )];
}"}}]}))))

(def ^:private max-quotes-id (atom (max-id-for quotes-bucket)))
(defn- quote-id []
  (str (swap! max-quotes-id inc)))


(defn create-quote! [quote]
  (ensure-db-connected)
  (let [id (quote-id)]
    (kv/store quotes-bucket id quote :content-type "application/json")
    (assoc quote :id id)))
