;--------------------------------------------------------------------------
; This tutorial introduces the following:
; 1. Queue Durability - The ability to service a system or service restart
; 2. Message Durability - The ability to service a system or service restart
; 3. Message Acknowledgement - Manually acknowledge the consumption of a msg
;--------------------------------------------------------------------------
(ns tutorial.workqueues
  (:use com.mefesto.wabbitmq)
  (:use tutorial.queue))

(def queuename "work.queue")
(def exchangename "work.exchange")
(def routing-key "")
(def persistent-delivery-mode 2) 

;----------------------
; Producer portion
;----------------------
(defn producer 
  []
  (with-broker {:host "localhost" :username "idiscc" :password "1d15cc"}
    (with-channel
      (qos 1)
      (create-queue queuename)
      (create-exchange exchangename "fanout")
      (queue-bind queuename exchangename routing-key)
      
      (with-exchange exchangename 
        (publish routing-key persistent-delivery-mode (.getBytes "First Task..") )
        (publish routing-key persistent-delivery-mode (.getBytes "Second Task."))
        (publish routing-key persistent-delivery-mode (.getBytes "Third Task....."))
        (publish routing-key persistent-delivery-mode (.getBytes "Fourth Task.."))
        )
  )))

;----------------------
; Consumer portion
;----------------------
(defn work
  [task]
  (. Thread (sleep (* 1000 (count (re-find #"\.+" task))))))

(defn consumer
  []
  (with-broker {:host "localhost" :username "idiscc" :password "1d15cc"}
    (with-channel
      (with-queue queuename 
         (doseq [msg (consuming-seq false)]
          (let [body (String. (:body msg))]
            (print "received: " body)
            (work body)
            (println ".Done!")
            (ack (:delivery-tag (:envelope msg)))))))))
