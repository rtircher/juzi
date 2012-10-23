(ns juzi.models.wall
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defrecord Wall [id name description created-at updated-at])

(defn wall [id]
  )

(defn create-wall! [{:keys [name description]}]
  (let [now (Date.)]
    (map->Wall (riak/store-wall! (Wall. nil name description now now)))))

(defn update-wall! [wall-id {:keys [name description]}]
  )

(defn delete-wall! [wall-id]
  )