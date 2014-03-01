(ns octopress-scraper-to-ghost.core
  (:require [net.cgrand.enlive-html :as html]
            [clj-time.format :as date]
            [clj-time.coerce]
            [clj-time.core :as core-date]
            [clojure.data.json :as json]
            [clj-http.client :as client]))

(def archive-relative "/blog/archives")
(def template-path "ghost-import-template.json")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn resolve-absolute [blog-url relative-a-tag]
  (str blog-url (:href (:attrs relative-a-tag))))

(defn extract-heading [raw-html] 
  (first (html/select raw-html [:article :h1])))

(defn extract-date-published [raw-html]
  (clj-time.coerce/to-long (date/parse (date/formatters :date-time-no-ms) (:datetime (:attrs (first(html/select raw-html [:time])))))))

(defn extract-markdown [raw-html]
  (:body 
    (client/post 
      "http://heckyesmarkdown.com/go/" 
      {:form-params {:html
        (apply str 
          (html/emit* 
            (first (html/select raw-html  [:div.entry-content])))) :read 0}})))

(defn extract-sections [blog-post-url]
  (let [raw-html (fetch-url blog-post-url)]
    {:heading (html/text (extract-heading  raw-html))
     :published-date (extract-date-published raw-html) 
     :markdown (extract-markdown raw-html)}))  

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


(defn jsonify-blog 
  ([blog-url] 
    (let 
      [json-template (slurp-template)       blog-post-template (first (get-in json-template [:data :posts])) ]
      (json/write-str
      (merge
        json-template 
        {:meta
          { :exported_on (clj-time.coerce/to-long (core-date/now))
            :version "000" }}
      {:data { :posts 
    (jsonify-blog 
      blog-url 
      (take 1 
        (html/select 
          (fetch-url (str blog-url archive-relative)) [:div#blog-archives :a]))
      []
      blog-post-template 1)}}
       {:tags [] }{:posts_tag [] }))))
  ([blog-url blog-post-links blog-content blog-post-template blog-post-id]
   (if (seq blog-post-links)
     (recur blog-url 
       (rest blog-post-links)
       (conj  
         blog-content 
         (merge-post-sections-into-template
          blog-post-template  
         (extract-sections 
           (resolve-absolute blog-url (first blog-post-links))  )blog-post-id))
         blog-post-template (inc blog-post-id))
     blog-content)))


