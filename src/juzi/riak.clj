(ns juzi.riak
  (:use juzi.config)
  (:require [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.kv   :as kv])
  ;(:import com.basho.riak.client.http.util.Constants)
  )

(def ^:private db-connected (ref false))

(defn- ensure-db-connected []
  (dosync 
   (when (not db-connected)
     (wc/connect! (:db-url config))
     (ref-set db-connected true))))

(defn create-quote! [quote]
  (ensure-db-connected)
  (kv/store "quotes" "key" quote :content-type "application/clojure"))

;; Need counter for each bucket to save current last id saved
;; This need to be threadsafe
;; Safe this in a riak bucket

;; Need to return stored object