(ns octopress-scraper-to-ghost.core-test
  (:use expectations 
        [octopress-scraper-to-ghost.core :refer :all]
        [clojure.data.json :as json]
        [clojure.java.io :as io]
        [net.cgrand.enlive-html :as html]))
(def blog-url "http://thephotographic.me.uk")

;; it can retrieve lists from archives

;; it can fetch html from urls
(expect "a sample blog" 
        (html/text 
          (first 
            (html/select  
              (fetch-html 
                (str (io/resource "test-blog-home-page.html"))) [:title]))))

;; it can extract elements
(expect "element content" 
        (html/text
          (extract-element
            (html/html-snippet "<html><title>element content</title</html>" )
            [:title])))

;;(expect "ghost-json" (jsonify-blog blog-url))

;;(spit "/Users/will/Desktop/output.json" (jsonify-blog blog-url))

