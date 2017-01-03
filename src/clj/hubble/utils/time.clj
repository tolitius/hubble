(ns hubble.utils.time
  (:import [java.time Instant ZoneId ZonedDateTime ZoneOffset Duration]
           [java.time.format DateTimeFormatter]
           [com.google.common.primitives Longs]))

(defonce BIG-BANG 0)                  ;; i.e. beginning of times (in this case it's the beginning of UNIX times..)

(defn current-utc-millis []
  (-> (ZoneOffset/UTC)
      (ZonedDateTime/now)
      (.toInstant)
      (.toEpochMilli)))

(defn from-now-millis [amount tu]
  (let [duration (Duration/of amount tu)]
    (-> (ZoneOffset/UTC)
        (ZonedDateTime/now)
        (.plus duration)
        (.toInstant)
        (.toEpochMilli))))

(defn str-now []
  (-> (ZonedDateTime/ofInstant
        (Instant/ofEpochMilli (current-utc-millis))
        (ZoneId/of "UTC"))
      (.format (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss.SSS"))))

(defn ms-to-hours [ms]
  (when ms
    (int (/ ms 3600000))))
