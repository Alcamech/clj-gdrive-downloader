(ns clj-gdrive-downloader.core
  (:require [clojure.spec.alpha :as spec]
            [clj-http.client :as client]
            [clj-http.cookies :refer [cookie-store]]
            [hickory.core :as hickory]
            [hickory.select :as selector]
            [clojure.java.io :as io]
            [me.raynes.fs :as filesys]
            [me.raynes.fs.compression :as compression])
  (:import (java.io File)
           (java.util.zip ZipOutputStream)))

(def cs (cookie-store))

(defn mkdirp
  "Create a directory and any missing parents."
  [path]
  {:pre [(spec/valid? string? path)]}
  (let [dir (io/file path)]
    (if (.exists dir)
      true
      (.mkdirs dir))))

(defn get-download-url
  "Get the download URL for the files located on the hosted google drive.
   The true download URL is embedded within the metadata of the request."
  [url]
  {:pre [(spec/valid? string? url)]
   :post [(spec/valid? string? %)]}
  (->> (client/get url {:as :html :cookie-store cs})
       :body
       hickory/parse
       hickory/as-hickory
       (selector/select (selector/id :uc-download-link))
       first
       :attrs
       :href))

(defn download-file
  "Download a .tgz or .zip file from google drive and places it in the dest-path."
  [filename dest-path url]
  {:pre [(spec/valid? string? filename)
         (spec/valid? string? dest-path)
         (spec/valid? string? url)]}
  (let [stream (as-> (client/get (str "https://drive.google.com" (get-download-url url))
                                 {:as :stream :cookie-store cs}) file-req
                     (:body file-req)
                     (cond
                       (= (filesys/extension filename) ".zip") (java.util.zip.ZipInputStream. file-req)
                       (= (filesys/extension filename) ".tgz") (java.util.zip.GZIPInputStream. file-req)))
        source (str dest-path File/separatorChar filename)]
    (if (= (type stream) java.util.zip.ZipInputStream)
      (with-open [zip (ZipOutputStream. (io/output-stream source))]
        (loop [entry (.getNextEntry stream)]
          (when entry
            (.putNextEntry zip entry)
            (io/copy stream zip)
            (.closeEntry zip)
            (recur (.getNextEntry stream)))))
      (io/copy stream (io/file source)))))

(defn download-file-from-google-drive
  "Downloads a shared compressed file from google drive into a given folder.
   Optionally decompresses it.

   Params:
   - filename: name + extensions you want to save the file as
   - dest-path: the destination to save the downloaded file
   - url: google drive download url
   - overwrite: optional, if true forces re-download and overwrite
   - extract: optional, if true will extract a compressed a file"
  [filename dest-path url & {:keys [overwrite extract]
                             :or {overwrite false, extract false}}]
  {:pre ([spec/valid? string? filename]
         [spec/valid? string? dest-path]
         [spec/valid? boolean? overwrite]
         [spec/valid? boolean? extract])}
  (let [source (str dest-path File/separatorChar filename)]
    (when (or (false? (.exists (io/as-file source))) (true? overwrite))
      (mkdirp dest-path)
      (println (str "Downloading " filename " into " dest-path))
      (download-file filename dest-path url)
      (when extract
        (println (str "Extracting " filename " into " dest-path))
        (cond
          (= (filesys/extension filename) ".zip") (compression/unzip source dest-path)
          (= (filesys/extension filename) ".tgz") (compression/untar source dest-path))))
    (println "File successfully downloaded.")))