;--------------------------------------------------------------------------
; This tutorial introduces the following:
; 1. The 'producer' writes to an exchange instead of to a queue
; 2. Makes use of temporary queues
; 3. All listeners/consumers get the message not just a single consumer
;--------------------------------------------------------------------------
(ns tutorial.pubsub
  (:use com.mefesto.wabbitmq)
  (:use tutorial.queue))

(def exchangename "pubsub.exchange")
(def routing-key "")

;----------------------
; Producer portion
;----------------------
(defn pubsub-producer []
  (with-broker con-info
    (with-channel
      (create-exchange exchangename "fanout")
      (with-exchange exchangename 
        (publish routing-key (.getBytes "First Log Entry."))))))

;----------------------
; Consumer portion
;----------------------
(defn pubsub-consumer  []
  (with-broker con-info
    (with-channel
      (def queuename (.queue (queue-declare)))
      (queue-bind queuename exchangename routing-key)
      (with-queue queuename
         (doseq [msg (consuming-seq false)]
          (let [body (String. (:body msg))]
            (println "received: " body)
            (println "routing-key: " (:routing-key (:envelope msg)))
            (ack (:delivery-tag (:envelope msg)))))))))

(defn run-pubsub [] 
  (pubsub-producer)
  (pubsub-consumer))