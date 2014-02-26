(ns octopress-scraper-to-ghost.core
  (:require [net.cgrand.enlive-html :as html]
            [clj-time.format :as date]))

(def archive-relative "/blog/archives")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn resolve-absolute [blog-url relative-a-tag]
  (str blog-url (:href (:attrs relative-a-tag))))

(defn extract-blog-post [blog-post-url]
  (let [heading (first (html/select (fetch-url blog-post-url) [:article :h1]))
        time-published (first(html/select (fetch-url blog-post-url) [:time]))
        body (first (html/select (fetch-url blog-post-url) [:div.entry-content]))]
     { 
       :title (html/text heading)
       :slug (html/text heading)
       :published_at (date/parse (date/formatters :date-time-no-ms) (:datetime (:attrs time-published)))
       :html (apply str (html/emit* body))}))

(defn jsonify-blog 
  ([blog-url] (jsonify-blog blog-url (take 2 (html/select (fetch-url (str blog-url archive-relative)) [:div#blog-archives :a])) []))
  ([blog-url blog-post-links blog-content]
   (if (seq blog-post-links)
     (recur blog-url 
            (rest blog-post-links)
            (conj  
              blog-content 
              (extract-blog-post (resolve-absolute blog-url (first blog-post-links)))))
     blog-content)))


