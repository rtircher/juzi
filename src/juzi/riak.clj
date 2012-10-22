(ns juzi.riak
  (:require [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.kv   :as kv])
  ;(:import com.basho.riak.client.http.util.Constants)
  )

(def ^:private db-connected (ref false))

(defn- ensure-db-connected []
  ;; TODO use config to connect to Riak db based dev, prod...
  ;; (wc/connect! "http://riak.data.megacorp.internal:8098/riak")
  (dosync 
   (when (not db-connected)
     (wc/connect! "http://127.0.0.1:8091/riak")
     (ref-set db-connected true)))
  )

(defn create-quote! [quote]
  (ensure-db-connected)
  (kv/store "quotes" "key" quote :content-type "application/clojure"))
