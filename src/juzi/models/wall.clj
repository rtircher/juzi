(ns juzi.models.wall)

(defn wall [id]
  {:id "random id" :quotes []})

(defn create-wall! []
  {:create "random id" :quotes []})

(defn update-wall! [wall-id]
  {:update wall-id :quotes []})
