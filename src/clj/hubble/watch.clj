(ns hubble.watch
  (:require [envoy.core :as envoy]
            [clojure.tools.logging :refer [info]]
            [mount.core :as mount :refer [defstate add-watcher on-change]]
            [hubble.env :refer [config]]))

(defn add-watchers []
  (let [watchers {:hubble/mission/target  [#'hubble.env/config #'hubble.core/mission]
                  :hubble/camera/mode     [#'hubble.env/config #'hubble.core/camera]
                  :hubble/store/url       [#'hubble.env/config #'hubble.core/store]}]
    (mount/restart-listener watchers)))

(defn watch-consul [path]
  (let [listener (add-watchers)]
    (info "watching on" path)
    (envoy/watch-path path #(on-change listener (keys %)))))

(defstate consul-watcher :start (watch-consul (apply str (vals                   ;; {:host "http://localhost:8500", :kv-prefix "/v1/kv", :app-path "/hubble"}
                                                           (config :consul))))   ;; to "http://localhost:8500/v1/kv/hubble"
                         :stop (envoy/stop consul-watcher))
