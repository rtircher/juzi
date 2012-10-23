(ns juzi.models.wall
  (:use [slingshot.slingshot :only [throw+]])
  (:require [juzi.riak :as riak])
  (:import java.util.Date))

(defrecord Wall [id name description created-at updated-at])

(defn- wall-not-found [wall-id]
  (throw+ {:type    :not-found
           :message (str "wall " wall-id " not found")}))

(defn wall [id]
  (if-let [wall (riak/find-wall id)]
    (map->Wall wall)
    (wall-not-found id)))

(defn create-wall! [{:keys [name description]}]
  (let [now (Date.)]
    (map->Wall (riak/store-wall! (Wall. nil name description now now)))))

(defn update-wall! [wall-id {:keys [name description]}]
  (let [created-at (:created-at (wall wall-id))]
    (map->Wall (riak/store-wall! wall-id (Wall. wall-id name description created-at (Date.))))))

(defn delete-wall! [wall-id]
  (let [wall (wall wall-id)]
    (riak/delete-wall! wall-id)
    wall))