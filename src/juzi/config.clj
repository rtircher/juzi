(ns juzi.config)

(defn- get-config-for-environement []
  (let [env (System/getProperty "APP_ENV" "development")
        env-config-ns (symbol (str "juzi.config." env))
        env-config (symbol (str "juzi.config." env "/config"))]
    (require env-config-ns)
    (deref (resolve env-config))))

(def config (get-config-for-environement))
