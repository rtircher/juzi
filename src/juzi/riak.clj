(ns juzi.riak
  (:use juzi.config)
  (:require [clojurewerkz.welle.core    :as wc]
            [clojurewerkz.welle.kv      :as kv]
            [clojurewerkz.welle.mr      :as mr])
  (:import com.basho.riak.client.http.util.Constants))

(def ^:private db-connected (ref false))
(def ^:private quotes-bucket "quotes")
(def ^:private walls-bucket "walls")

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
    },
    '0'
  )];
}"}}]}))))

;; create macro dbfn that contains the call to (ensure-db-connected)
;; and then just apply the body


(def ^:private max-walls-id (atom (max-id-for walls-bucket)))
(defn- next-wall-id []
  (str (swap! max-walls-id inc)))

(defn store-wall!
  ([wall]
     (store-wall! (next-wall-id) wall))
  ([id wall]
     (ensure-db-connected)
     (kv/store walls-bucket id wall
               :content-type Constants/CTYPE_JSON_UTF8)
     (assoc wall :id id)))

(defn find-wall [wall-id]
  (ensure-db-connected)
  (if-let [wall (kv/fetch-one walls-bucket wall-id)]
    (assoc (:value wall) :id wall-id)))



(def ^:private max-quotes-id (atom (max-id-for quotes-bucket)))
(defn- next-quote-id []
  (str (swap! max-quotes-id inc)))

(defn store-quote!
  ([quote]
     (store-quote! (next-quote-id) quote))
  ([id quote]
     (ensure-db-connected)
     (kv/store quotes-bucket id quote
               :content-type Constants/CTYPE_JSON_UTF8
               :indexes {:wall-id (:wall-id quote)})
     (assoc quote :id id)))

(defn find-quote [quote-id]
  (ensure-db-connected)
  (if-let [quote (kv/fetch-one quotes-bucket quote-id)]
    (assoc (:value quote) :id quote-id)))

(defn find-quotes [wall-id]
  (ensure-db-connected)
  (pmap find-quote (kv/index-query quotes-bucket :wall-id wall-id)))

(defn delete-quote! [quote-id]
  (ensure-db-connected)
  (kv/delete quotes-bucket quote-id))
