(ns tutorial.helloworld
   (:use com.mefesto.wabbitmq)
   (:use tutorial.queue))

(def queuename "helloworld.queue")
(def exchangename "helloworld.exchange")

;----------------------
; Producer portion
;----------------------
(defn helloworld-producer 
  []
  (with-broker con-info
    (with-channel
      (create-queue queuename)
      (create-exchange exchangename "fanout")
      (queue-bind queuename exchangename "")

      (with-exchange exchangename 
        (publish "" (.getBytes "Hello World!")))
  )))

;----------------------
; Consumer portion
;----------------------
(defn helloworld-consumer
  []
  (with-broker con-info
    (with-channel
      (with-queue queuename
         (doseq [msg (consuming-seq true)]
           (println "received: " (String. (:body msg))))))))


(defn run-helloworld []
   (helloworld-producer)
   (helloworld-producer)
   (helloworld-producer)
  
   ;We now have 3 messages queued up so lets 'consume' them
   (helloworld-consumer))