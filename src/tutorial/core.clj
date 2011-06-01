(ns tutorial.core
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:use [clojure.contrib.seq-utils :only (includes?)])
  (:use [clojure.contrib.str-utils :only (str-join)])
  (:use tutorial.helloworld)
  (:use tutorial.pubsub)
  (:use tutorial.routing)
  (:use tutorial.topic)
  (:use tutorial.workqueues))

(def valid-tuts (seq ["hello" "work" "pubsub" "route" "topic"]))
(def valid-roles (seq ["c" "p"]))

(defn exit-info []
   (println "To exit enter ctrl-c"))

(defn hello [role]
   (if (= role "c") 
     (do (println "Calling helloworld-consumer...") (exit-info) (helloworld-consumer)) 
     (do (print "Calling helloworld-producer...")(helloworld-producer) (println "Done!"))))	

(defn work [role]
   (if (= role "c") 
     (do (println "Calling workqueues-consumer...") (exit-info) (workqueues-consumer)) 
     (do (print "Calling workqueues-producer...") (workqueues-producer) (println "Done!"))))

(defn publish [role]
   (if (= role "c") 
     (do (println "Calling pubsub-consumer...") (exit-info) (pubsub-consumer)) 
     (do (print "Calling pubsub-producer...") (pubsub-producer) (println "Done!"))))

(defn route [role]
   (if (= role "c") 
     (do (println "Calling routing-consumer...") (exit-info) (routing-consumer))
     (do (print "Calling routing-producer...") (routing-producer) (println "Done!"))))

(defn topics [role]
   (if (= role "c") 
     (do (println "Calling topic-consumer...") (exit-info) (topic-consumer))
     (do (print "Calling topic-producer...") (topic-producer) (println "Done!"))))

(defn process-args [tut role]
  (if (and (includes? valid-tuts tut) (includes? valid-roles role))
   (cond
     (= tut "hello") (hello role)
     (= tut "work" ) (work role)
     (= tut "pubsub") (publish role)
     (= tut "route") (route role)
     (= tut "topic") (topics role)
   )
   (println "Invalid parameter values.\nValid tut values are " (str-join ", " valid-tuts) "\nand valid roles are " (str-join ", " valid-roles))))

(defn -main [& args]
   (with-command-line args
     "An interface to all of the RabbintMQ tutorials I converted to clojure"
     [[tut "which toturial to run. (hello, work, publish, route, topics)"]
      [role "which role to play consumer or producer (c,p)"]
       remaining]
   (process-args tut role)))
