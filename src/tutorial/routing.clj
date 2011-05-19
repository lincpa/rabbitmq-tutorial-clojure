;--------------------------------------------------------------------------
; This tutorial introduces the following:
; 1. Using routing-key's as a way to filter out messages 
;--------------------------------------------------------------------------
(ns tutorial.routing
  (:use com.mefesto.wabbitmq)
  (:use tutorial.queue))

(def exchangename "routing.exchange")
(def error-routing-key "error")
(def warning-routing-key "warning")
(def info-routing-key "info")

;----------------------
; Producer portion
;----------------------
(defn emit-log-direct 
  []
  (with-broker con-info
    (with-channel
      (create-exchange exchangename "direct")
      (with-exchange exchangename 
        (publish error-routing-key (.getBytes "error Route Entry."))
        (publish warning-routing-key (.getBytes "warning Route Entry."))
        (publish info-routing-key (.getBytes "info Route Entry."))
        (publish error-routing-key (.getBytes "error Route Entry."))))))

;----------------------
; Consumer portion
;----------------------
(defn receive-logs-direct
  []
  (with-broker con-info
    (with-channel
      (def queuename (.queue (queue-declare)))
      (queue-bind queuename exchangename error-routing-key)
      (queue-bind queuename exchangename warning-routing-key)
      ;(queue-bind queuename exchangename info-routing-key)
      (with-queue queuename
         (doseq [msg (consuming-seq false)]
          (let [body (String. (:body msg))]
            (println "received: " body)
            (println "routing-key: " (:routing-key (:envelope msg)))
            (ack (:delivery-tag (:envelope msg)))))))))
