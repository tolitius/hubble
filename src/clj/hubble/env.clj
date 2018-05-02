(ns hubble.env
  (:require [mount.core :refer [defstate]]
            [cprop.core :refer [load-config]]
            [cprop.source :refer [from-system-props
                                  from-env]]
            [envoy.core :as envoy]
            [clojure.tools.logging :as log]
            [hubble.tools.vault :as vault]))

(defn to-consul-path
  "consul config to 'host/kv-prefix/path'"
  [cconf]
  (->> cconf
       vals
       (apply str)))

(defn with-creds [conf at token]
  (-> (vault/merge-config conf {:at at
                                :vhost [:hubble :vault :url]
                                :token token})
      (get-in at)))

(defn create-config []
  (let [conf (load-config :merge [(from-system-props)
                                  (from-env)])]
    (->> (conf :consul)
         to-consul-path
         (envoy/merge-with-consul conf))))

(defstate config :start (create-config))





;; playground

(defn init-consul
  "load config to consul without consul props and creds
   this is done once (usually before the app is deployed),
   and here is mostly for demo / repro purposes"
  []
  (let [{:keys [] {host :host
                   kv :kv-prefix} :consul :as conf} (load-config)
        cpath (str host kv)]
    (log/info "initializing Consul at" cpath)
    (as-> (dissoc conf :consul) $
      (update-in $ [:hubble :log] dissoc :auth-token)
      (update-in $ [:hubble :log :hazelcast] dissoc :group-name
                                                    :group-password)
      (envoy/map->consul cpath $))))
