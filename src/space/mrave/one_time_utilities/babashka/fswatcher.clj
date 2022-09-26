(ns space.mrave.one-time-utilities.babashka.fswatcher
  (:require [babashka.fs :as fs]
            [babashka.pods :as pods]))

(pods/load-pod 'org.babashka/fswatcher "0.0.3")

(require '[pod.babashka.fswatcher :as fw])

(defn watch-file
  "Monitor a single file for file system events"
  [file-to-watch & {cb :callback
                       action :type
                       :or {action :all
                            cb #(println "caught" %)}}]
  (let [parent (fs/parent (fs/file file-to-watch))]
    (fw/watch (str parent)
              (fn
                [event]
                (when (and (or (= action :all) (= action (:type event)))
                           (= (fs/path file-to-watch) (fs/path (:path event))))
                  (cb event))))))

(defn unwatch
  [watcher]
  (fw/unwatch watcher))

