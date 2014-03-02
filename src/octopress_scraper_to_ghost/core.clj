(ns octopress-scraper-to-ghost.core
  (:require [net.cgrand.enlive-html :as html]
            [clj-time.format :as date]
            [clj-time.coerce]
            [clj-time.core :as core-date]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojure.java.io :as io])
  (:import [com.overzealous.remark Remark Options])
  (:gen-class :main true))

(def archive-relative-path "/blog/archives")
(def template-path "ghost-import-template.json")

(defn fetch-html [url]
  (html/html-resource (java.net.URL. url)))

(defn extract-element [raw-html path]
  (first (html/select raw-html path)))

(defn to-text [html-element] 
  (html/text html-element))

(defn to-unix-epoch [html-element]
  (str
  (clj-time.coerce/to-long 
    (date/parse 
      (date/formatters :date-time-no-ms) 
      (:datetime (:attrs html-element))))))

(defn to-markdown [html-element]
  (let [opts (Options/markdown)]
    (set! (.simpleLinkIds opts) true)
    (.convertFragment (Remark. opts) (apply str(html/emit* html-element)))))

(defn extract-sections [post-url]
  (let [raw-html (fetch-html post-url)]
    {:heading (to-text (extract-element raw-html [:article :h1]))
     :published-date (to-unix-epoch (extract-element raw-html [:time]))
     :markdown (to-markdown (extract-element raw-html [:div.entry-content]))}))  
(defn merge-post-sections-into-template 
  [blog-post-template post-sections blog-post-id]
  (merge
    blog-post-template 
    { 
     :id blog-post-id
     :title (:heading post-sections)
     :meta_title (:heading post-sections)
     :slug (:heading post-sections)
     :published_at (:published-date post-sections)
     :updated_at (:published-date post-sections)
     :created_at (:published-date post-sections)
     :markdown (:markdown post-sections)
     }))

(defn slurp-template []
  (json/read-json (slurp (clojure.java.io/resource template-path))))

(defn generate-meta []
  {:meta
   { :exported_on (clj-time.coerce/to-long (core-date/now))
    :version "000" }})

(defn extract-blog-post-template [whole-template]
  (first (get-in whole-template [:data :posts])))

(defn extract-blog-post-urls [blog-url]
  (take 1 
        (for 
          [relative 
           (html/select (fetch-html (str blog-url archive-relative-path)) [:div#blog-archives :a])]
          (str blog-url (:href (:attrs relative)))
          )))

(defn jsonify-posts 
  ([blog-url blog-post-template]
   (jsonify-posts
     (extract-blog-post-urls blog-url)
     []
     blog-post-template
     1))
  ([remaining-post-urls jsonified-posts post-template post-id]
   (if (seq remaining-post-urls)
     (let [this-post (merge-post-sections-into-template
                       post-template  
                       (extract-sections 
                         (first remaining-post-urls))post-id)]
       (recur (rest remaining-post-urls)
              (conj jsonified-posts this-post)
              post-template
              (inc post-id)))
     jsonified-posts)))

(defn jsonify-blog [blog-url] 
  (let 
    [json-template (slurp-template)
     blog-post-template (extract-blog-post-template json-template) ]
    (json/write-str
      (merge
        json-template 
        (generate-meta) 
        {:data { :posts 
                (jsonify-posts blog-url blog-post-template)}}
        {:tags [] }{:posts_tag [] }))))

(defn -main
    "The application's main function"
    [& args]
    (println args))

