(ns octopress-scraper-to-ghost.core
  (:require [net.cgrand.enlive-html :as html]
            [clj-time.format :as date]
            [clojure.data.json :as json]))

(def archive-relative "/blog/archives")
(def template-path "ghost-import-template.json")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn resolve-absolute [blog-url relative-a-tag]
  (str blog-url (:href (:attrs relative-a-tag))))

(defn extract-blog-post [blog-post-url blog-post-template]
  (let [heading (first (html/select (fetch-url blog-post-url) [:article :h1]))
        time-published (first(html/select (fetch-url blog-post-url) [:time]))
        body (first (html/select (fetch-url blog-post-url) [:div.entry-content]))]  blog-post-template 
    (merge
      blog-post-template 
    { 
     :title (html/text heading)
     :_meta_title (html/text heading)
     :slug (html/text heading)
     :published_at (date/parse (date/formatters :date-time-no-ms) (:datetime (:attrs time-published)))
     :html (apply str (html/emit* body))})
    ))

(defn jsonify-blog 
  ([blog-url] 
    (let 
      [json-template (json/read-json (slurp (clojure.java.io/resource template-path)))
       blog-post-template (first (get-in json-template [:data :posts])) ]
    (jsonify-blog 
      blog-url 
      (take 5 
        (html/select 
          (fetch-url (str blog-url archive-relative)) [:div#blog-archives :a]))
      []
      blog-post-template)))
  ([blog-url blog-post-links blog-content blog-post-template]
   (if (seq blog-post-links)
     (recur blog-url 
       (rest blog-post-links)
       (conj  
         blog-content 
         (extract-blog-post 
           (resolve-absolute blog-url (first blog-post-links)) blog-post-template))
         blog-post-template)
     blog-content)))


