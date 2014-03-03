(ns octopress-scraper-to-ghost.core-test
  (:use expectations 
        [octopress-scraper-to-ghost.core :refer :all]
        [clojure.data.json :as json]
        [clojure.java.io :as io]
        [clj-time.core :as core-date]
        [net.cgrand.enlive-html :as html]))

(def blog-url "http://thephotographic.me.uk")
(def dummy-blog-post-template 
  (extract-blog-post-template 
    (slurp-template)))

;; it can retrieve  from archives

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

;; can merge blog posts with template appropriately
(expect {:status "published", 
         :slug :dummy-heading, 
         :featured 0, 
         :meta_description nil, 
         :updated_at 
         :dummy-date, 
         :created_by 1, 
         :page 0, 
         :updated_by 1, 
         :image nil, 
         :language "en_GB", 
         :title :dummy-heading, 
         :created_at :dummy-date, 
         :markdown :dummy-markdown, 
         :meta_title :dummy-heading, 
         :published_by 1, 
         :author_id 1, :id 123, 
         :published_at :dummy-date}
        (let [dummy-post-sections {:heading :dummy-heading 
                                   :published-date :dummy-date 
                                   :markdown :dummy-markdown} 
              dummy-post-id 123 ] 
          (merge-post-sections-into-template 
            dummy-blog-post-template 
            dummy-post-sections 
            dummy-post-id))) 

;; can slurp in json templates
(expect {:meta {:exported_on "test", :version "000"}, :data {:posts [{:status "published", :featured 0, :meta_description nil, :created_by 1, :page 0, :updated_by 1, :image nil, :language "en_GB", :title "slug", :published_by 1, :author_id 1}], :tags [], :posts_tags []}} (slurp-template))

;; can generate metadata
(expect {:meta {:exported_on 1393794804000, :version "000"}}
       (with-redefs 
         [core-date/now (fn [] (core-date/date-time 2014 03 02 21 13 24))]
         (generate-meta)))


;;(expect "ghost-json" (jsonify-blog blog-url))

;;(spit "/Users/will/Desktop/output.json" (jsonify-blog blog-url))

