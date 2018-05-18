(ns smeagol.include.parse
  (:require
    [schema.core :as s]))

(def IncludeLink
  {:uri s/Str
   :indent-heading s/Num
   :indent-list s/Num})

(s/defn
  convert-indent-to-int :- s/Num
  [indents :- [s/Str]]
  (if (some? indents)
      (Integer/valueOf (nth indents 2))
      0))

(s/defn
  parse-indent-list
  [md-src :- s/Str]
  (re-matches #".*(:indent-list (\d)).*" md-src))

(s/defn
  parse-indent-heading
  [md-src :- s/Str]
  (re-matches #".*(:indent-heading (\d)).*" md-src))

(s/defn
  parse-include-link
  [md-src :- s/Str]
  (re-seq #".*&\[\w*(.*)\w*\]\((.*)\).*" md-src))

(s/defn
  parse-include-md :- [IncludeLink]
  [md-src :- s/Str]
  (vec
    (map
      (fn [parse-element]
        (let [uri (nth parse-element 2)
              indents-text (nth parse-element 1)]
          {:uri uri
           :indent-heading (convert-indent-to-int (parse-indent-heading indents-text))
           :indent-list (convert-indent-to-int (parse-indent-list indents-text))}))
      (parse-include-link md-src))))
