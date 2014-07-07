# cayley-clj

A clojure library for working with Google's Cayley graph database.

## Status

Currently, the library only supports the Javascript/Gremlin query syntax.

## Usage

Leiningen project.clj dependency:
[![Clojars Project](http://clojars.org/org.clojars.wgb/cayley-clj/latest-version.svg)](http://clojars.org/org.clojars.wgb/cayley-clj)

Queries are formed using syntax similar to Javascript/Gremlin. A query takes the form of a vector of vectors. The subvectors are in the form of [:verb & args] where verb is a keyword with optional args that can be either a string or integer value, an empty vector (call verb with no args), a vector of values, or a vector of the form [:verb & args].

For writes, triples/quads take the form of [subject predicate object provenance]. Provenance is optional. The write function takes a collection of triples/quads and the write url.

## Example
Query
------
Using the 30kmovies NQuads file that comes with the Cayley distribution we would query Cayley like so:

```clojure
(ns some.namespace
(:require [cayley-clj.core :as cayley]))
;; some fn...
(cayley/query [[:g][:V []][:Has ["name" "Burt Reynolds"]][:In "/film/performance/actor"][:In "/film/film/starring"][:Tag "filmID"][:Out "name"][:Tag "name"][:Back "filmID"][:Out "type"][:All []]] "http://localhost:64210/api/v1/query/gremlin")
```
This runs the query against Cayleys REST API as:
```
g.V().Has("name","Burt Reynolds").In("/film/performance/actor").In("/film/film/starring").Tag("filmID").Out("name").Tag("name").Back("filmID").Out("type").All()
```
#### Query Paths
Query also takes optional path/query arguments that are bound to vars in the query.
Paths/Queries take the form of:
```clojure
["varname" [[:g][:Morphism []]...]]
```

Paths/Queries are referenced in the query by the string name that preceeds the path form. So, they are referenced with the "Follow", "FollowR", "Intersect", and "Union" verbs.

#### Response
##### Success
```clojure
{:result [{:key "val" :id "val"}...]}
```

##### Error
Throws ex-info whose data is a map of the form:
```clojure
{:error response-body
 :http-context {:header headers
                :body request-body
                :http-status response-status-code
                :full-response full-response-object}}
```

Writes
------
```clojure
(cayley/write [["subject" "predicate" "object"]["subject" "predicate" "object" "provenance"]...] "http://localhost:64210/api/v1/write")
```

#### Response
##### Success
```clojure
{:result "Success message."}
```

##### Error
Throws ex-info whose data is a map of the form:
```clojure
{:error response-body
 :http-context {:header headers
                :body request-body
                :http-status response-status-code
                :full-response full-response-object}}
```

## TODO
1. Tests
