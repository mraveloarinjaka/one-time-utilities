(ns space.mrave.one-time-utilities.babashka.fswatcher
  (:require [babashka.fs :as fs]
            [babashka.pods :as pods]))

(pods/load-pod 'org.babashka/fswatcher "0.0.3")

(require '[pod.babashka.fswatcher :as fw])

(ns-unalias *ns* 'watch)

(defmulti watch
  "Monitor a resource for file system events"
  (fn [& args] (let [source (first args)]
                 (if (fs/directory? source) :dir :file)))
  :default :file)

(defn relevant?
  [monitored-action event]
  (or (= monitored-action :all) (= monitored-action (:type event))))

(defmethod watch :file
  watch-file
  [file-to-watch & {cb :callback
                    action :type
                    :or {action :all
                         cb #(println "caught" %)}}]
  (println "watching file" file-to-watch)
  (let [parent (fs/parent (fs/file file-to-watch))]
    (fw/watch (str parent)
              (fn
                [event]
                (when (and (relevant? action event)
                           (= (fs/path file-to-watch) (fs/path (:path event))))
                  (cb event))))))

(defmethod watch :dir
  watch-dir
  [dir-to-watch & {cb :callback
                   action :type
                   :or {action :all
                        cb #(println "caught" %)}}]
  (println "watching directory" dir-to-watch)
  (fw/watch (str dir-to-watch)
            (fn
              [event]
              (when (relevant? action event)
                (cb event)))))

(defn unwatch
  [watcher]
  (fw/unwatch watcher))

(def watch-file (get-method watch :file))

(comment
  
  (def watched-file (watch "D:\\code.py" :type :write))

  (unwatch watched-file)

  (def watched-dir (watch "D:\\" :type :write))

  (unwatch watched-dir)


  )
