(ns hubble.tools.vault
  (:require [vault.client :as vault]
            [cheshire.core :as json]))

(defn with-creds [vault-url token]
  (let [conn (vault/http-client vault-url)]
    (vault/authenticate! conn :token token)
    (try
      (-> (vault/read-secret conn "cubbyhole/response")
          :response
          (json/parse-string true)
          :data :value
          (json/parse-string true))
      (catch Exception e
        (throw (ex-info "could not read creds from Vault"
                        {:vault-url vault-url
                         :token token}
                        e))))))
