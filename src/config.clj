(ns config)

(def defconfig identity)

(defn- get-config-for-environement []
  (let [env (System/getProperty "APP_ENV" "development")
        env-config (str "src/config/" env ".clj")]
    (load-file env-config)))

(def config (get-config-for-environement))
