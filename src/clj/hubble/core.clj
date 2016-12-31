(ns hubble.core
  (:require [mount.core :refer [defstate]]
            [hubble.env :refer [config]]))

;; in reality these would be trully stateful components:
;;    i.e. with a camera state
;;         connection to a store
;;         connection to a mission knowledge base
;;          etc.

(defstate camera :start {:on? true 
                         :settings (get-in config [:hubble :camera])}
                 :stop {:on? false})

(defstate store :start {:connected-to (get-in config [:hubble :store])}
                :stop {:connected-to nil})

(defstate mission :start {:active true :details (get-in config [:hubble :mission])}
                  :stop {:active false})
