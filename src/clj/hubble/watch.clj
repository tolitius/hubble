(ns hubble.watch
  (:require [envoy.core :as envoy]
            [clojure.tools.logging :refer [info]]
            [mount.core :as mount :refer [defstate add-watcher on-change]]
            [hubble.env :as env]))

(defn add-watchers []
  (let [watchers {:hubble/mission/target  [#'hubble.env/config #'hubble.core/mission]
                  :hubble/camera/mode     [#'hubble.env/config #'hubble.core/camera]
                  :hubble/store/url       [#'hubble.env/config #'hubble.core/store]}]
    (mount/restart-listener watchers)))

(defn watch-consul [path]
  (let [listener (add-watchers)]
    (info "watching on" path)
    (envoy/watch-path path #(on-change listener (keys %)))))

(defstate consul-watcher :start (watch-consul (env/to-consul-path
                                                (env/config :consul)))
                         :stop (envoy/stop consul-watcher))
