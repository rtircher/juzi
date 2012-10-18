(ns juzi.models.quote)

(defn quotes [wall-id]
  {:quotes ["a quote" "another quote"]})

(defn create-quote! [wall-id quote-text]
  {:create [quote-text]})

(defn update-quote! [wall-id quote-id quote-text]
  {:update [quote-text]})
