;--------------------------------------------------------------------------
; This tutorial introduces the following:
; 1. Queue Durability - The ability to service a system or service restart
; 2. Message Durability - The ability to service a system or service restart
; 3. Message Acknowledgement - Manually acknowledge the consumption of a msg
;--------------------------------------------------------------------------
(ns tutorial.workqueues
  (:use com.mefesto.wabbitmq)
  (:use tutorial.queue))

(def wq-queuename "work.queue")
(def wq-exchangename "work.exchange")
(def routing-key "")
(def persistent-delivery-mode 2) 

;----------------------
; Producer portion
;----------------------
(defn workqueues-producer 
  []
  (with-broker con-info
    (with-channel
      (qos 1)
      (create-queue wq-queuename)
      (create-exchange wq-exchangename "fanout")
      (queue-bind wq-queuename wq-exchangename routing-key)
      
      (with-exchange wq-exchangename 
        (publish routing-key persistent-delivery-mode (.getBytes "First Task..") )
        (publish routing-key persistent-delivery-mode (.getBytes "Second Task."))
        (publish routing-key persistent-delivery-mode (.getBytes "Third Task....."))
        (publish routing-key persistent-delivery-mode (.getBytes "Fourth Task.."))
        )
  )))

;----------------------
; Consumer portion
;----------------------
(defn wq-work
  [task]
  (. Thread (sleep (* 1000 (count (re-find #"\.+" task))))))

(defn workqueues-consumer
  []
  (with-broker con-info
    (with-channel
      (with-queue wq-queuename 
         (doseq [msg (consuming-seq false)]
          (let [body (String. (:body msg))]
            (print "received: " body)
            (wq-work body)
            (println ".Done!")
            (ack (:delivery-tag (:envelope msg)))))))))


(defn run-workqueues []
  (workqueues-producer)
  (workqueues-consumer))