(ns hubble.core
  (:require [mount.core :refer [defstate]]
            [hubble.env :as env]
            [hubble.utils.time :as dt]
            [chazel.core :as hz]
            [chazel.serializer :as ser]))

;; in reality these would be trully stateful components (i.e. like the "mission-log" below):
;;    i.e. with a camera state
;;         connection to a store
;;         connection to a mission knowledge base
;;         etc.

(defstate camera :start {:on? true
                         :settings (get-in env/config [:hubble :camera])}
                 :stop {:on? false})

(defstate store :start {:connected-to (get-in env/config [:hubble :store])}
                :stop {:connected-to nil})

(defstate mission :start {:active true
                          :details (get-in env/config [:hubble :mission])}
                  :stop {:active false})


;; hubble mission log

(defstate mission-log :start (hz/client-instance (env/with-creds env/config
                                                                 [:hubble :log :hazelcast]
                                                                 [:hubble :log :auth-token]))
                      :stop (hz/shutdown-client mission-log))

(defn document! [log {:keys [name] :as event}]
  (when-not (= name "#'hubble.env/config")   ;; remove conf for the demo
    (hz/put! log
             (dt/current-utc-millis) event
             ser/transit-out)))

