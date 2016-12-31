(ns hubble.app
  (:require [mount.core :as mount :refer [defstate]]
            [mount-up.core :as register :refer [log]]
            [hubble.env :as env]
            [hubble.core]
            [hubble.watch]
            [hubble.server :refer [broadcast-to-clients! http-server]])
  (:gen-class))  ;; for -main / uberjar (no need in dev)

;; example of an app entry point
(defn -main [& args]

  ;; registering "log" to "info" ":before" every time mount states start and stop
  (register/on-upndown :info log :before)

  (env/init-consul)   ;; in "reality" data would already be in consul (i.e. no need to init)
  (mount/start)

  ;; registering "notify" to notify browser clients :after every state start
  (register/on-up :push #(broadcast-to-clients! http-server %)
                  :after))
