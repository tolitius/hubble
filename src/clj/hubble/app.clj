(ns hubble.app
  (:require [mount.core :as mount :refer [defstate]]
            [mount-up.core :as register :refer [log]]
            [chazel.core :as hz]
            [hubble.env :as env]
            [hubble.core :as hubble]
            [hubble.watch]
            [hubble.server :refer [broadcast-to-clients! http-server]])
  (:gen-class))  ;; for -main / uberjar (no need in dev)

(defn- add-mission-log [log-name]
  (mount/start #'hubble.core/mission-log)

  (let [mlog (hz/hz-map log-name
                        hubble/mission-log)]
    (register/on-up :mission-log #(hubble/document! mlog %)
                    :after)))

;; example of an app entry point
(defn -main [& args]

  ;; registering "log" to "info" ":before" every time mount states start and stop
  (register/on-upndown :info log :before)

  ;; start without a mission log by default
  (mount/start-without #'hubble.core/mission-log)

  ;; in case the mission log is enabled, add it to the app
  (let [{:keys [enabled name]} (get-in env/config
                                       [:hubble :log])]
    (when enabled
      (add-mission-log name)))

  ;; registering "notify" to notify browser clients :after every state start
  (register/on-up :notify-clients #(broadcast-to-clients! http-server %)
                  :after))
