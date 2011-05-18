(ns tutorial.helloworld
   (:use com.mefesto.wabbitmq)
   (:use tutorial.queue))

(def queuename "helloworld.queue")
(def exchangename "helloworld.exchange")

;----------------------
; Producer portion
;----------------------
(defn producer 
  []
  (with-broker {:host "localhost" :username "idiscc" :password "1d15cc"}
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
(defn consumer
  []
  (with-broker {:host "localhost" :username "idiscc" :password "1d15cc"}
    (with-channel
      (with-queue queuename
         (doseq [msg (consuming-seq true)]
           (println "received: " (String. (:body msg))))))))
