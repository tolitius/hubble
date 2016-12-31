(ns hubble.tools.consul
  (:require [cprop.tools :as cp]
            [envoy.core :as envoy]
            [clojure.tools.logging :as log]))

(defn merge-config [conf cpath]
  (let [{:keys [host kv-prefix app-path]} (get-in conf cpath)
        path (str host kv-prefix app-path)
        consul (envoy/consul->map path)]
    (if consul
      (do
        (log/info "merging configs with values in Consul:" path)
        (-> (cp/merge-maps conf consul)))
      (do
        (log/warn "found nothing in Consul to merge the config with. looked in consul path: [" path "]")
        conf))))
