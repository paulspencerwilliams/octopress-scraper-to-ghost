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

(defn element-resource-with-content [content]
  (first (html/select (html/html-snippet (str "<div>" content "</div>")) [:div])))
;; it can get text out of elements including markdown
(expect "element content" (to-text (element-resource-with-content "element content")))
(expect "# heading #\n\n[search here..][1]\n\n\n[1]: http://www.google.com" 
        (to-markdown 
          (element-resource-with-content 
            "<h1>heading</h1><a href=\"http://www.google.com\">search here..</a>")))

;; it can get published times out of <time>
(expect "1393794804000"
        (to-unix-epoch 
          (extract-element 
            (html/html-snippet "<time datetime=\"2014-03-02T21:13:24+00:00\" pubdate data-updated="true">Apr 10<span>th</span>, 2012</time>") [:time])))

;; it can extract all sections
(expect 
  {:heading :dummy-heading 
   :published-date :dummy-date 
   :markdown :dummy-markdown}
  (with-redefs [fetch-html (fn [a] :dummy-html)
                extract-element (fn [b c] :dummy-element)
                to-text (fn [d] :dummy-heading )
                to-unix-epoch (fn [e] :dummy-date )
                to-markdown (fn [f] :dummy-markdown )]
     (extract-sections :dummy-url)
    ))


;;(expect "ghost-json" (jsonify-blog blog-url))

;;(spit "/Users/will/Desktop/output.json" (jsonify-blog blog-url))

