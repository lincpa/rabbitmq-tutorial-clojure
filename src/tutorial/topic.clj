;--------------------------------------------------------------------------
; This tutorial introduces the following:
; 1. Using the topic type of exchange
; 2. A way to have more than one filter on messages
;--------------------------------------------------------------------------
(ns tutorial.topic
  (:use com.mefesto.wabbitmq)
  (:use tutorial.queue))

(def exchangename "topic.exchange")
(def esig-routing-key "esig.error")
(def idiscc-routing-key "idiscc.warning")
(def cine-routing-key "cine.info")

;----------------------
; Producer portion
;----------------------
(defn topic-producer  []
  (with-broker con-info
    (with-channel
      (create-exchange exchangename "topic")
      (with-exchange exchangename 
        (publish esig-routing-key (.getBytes "Esig Error topic Entry."))
        (publish idiscc-routing-key (.getBytes "iDiscc topic Entry."))
        (publish cine-routing-key (.getBytes "info topic Entry."))
        (publish "Nonsense.key" (.getBytes "Shouldn't see this message."))))))

;----------------------
; Consumer portion
;----------------------
(defn topic-consumer
  []
  (with-broker con-info
    (with-channel
      (def queuename (.queue (queue-declare)))
      (queue-bind queuename exchangename esig-routing-key)
      (queue-bind queuename exchangename "*.warning")
      (with-queue queuename
         (doseq [msg (consuming-seq false)]
          (let [body (String. (:body msg))]
            (println "received: " body)
            (println "routing-key: " (:routing-key (:envelope msg)))
            (ack (:delivery-tag (:envelope msg)))))))))

(defn run-topic []
  (topic-producer)
  (topic-consumer))