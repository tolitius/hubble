(ns hubble.consul
  (:require [mount.core :as mount :refer [defstate]]
            [envoy.core :as envoy]
            [clojure.edn :as edn]
            [clojure.tools.logging :refer [info]]))

(defn load-config [path]
  (info "loading config from" path)
  (-> path 
      slurp 
      edn/read-string))

(defn consul-host-from
  "reading a consul host kv prefix and the app path form the config"
  [path]
  (let [{:keys [] {host :host
                   kv-prefix :kv-prefix
                   path :app-path} :consul} (load-config path)]
    (str host kv-prefix path)))

(defstate config 
  :start (let [host (consul-host-from "resources/config.edn")]
           (assoc (envoy/consul->map host) :consul host)))




;; playground

(defn init-consul
  "load config to consul without consul props
   this is done once (usually before the app is deployed),
   and here is mostly for demo / repro purposes"
  [path]
  (let [{:keys [] {host :host
                   kv :kv-prefix} :consul :as conf} (load-config path)
        cpath (str host kv)]
    (envoy/map->consul cpath (dissoc conf :consul))))
