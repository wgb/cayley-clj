# cayley-clj

A clojure library for working with Google's Cayley graph database.

## Status

Currently, the library only supports the Javascript/Gremlin query syntax. Writes are coming soon.

## Usage

Leiningen project.clj dependency:
```clojure
[org.clojars.wgb/cayley-clj "0.1.0"]
```


Queries are formed using syntax similar to Javascript/Gremlin. A query takes the form of a vector of vectors. The subvectors are in the form of [verb args] where verb is either a string with no args, or a keyword with optional args that can be either a string or integer value, a vector of values, or a vector of the form [:verb & args].

## Example

Using the 30kmovies NQuads file that comes with the Cayley distribution we would query Cayley like so:

```clojure
(ns some.namespace
(:require [cayley-clj.core :as cayley]))
;; some fn...
(cayley/query [["g"][:V][:Has ["name" "Burt Reynolds"]][:In "/film/performance/actor"][:In "/film/film/starring"][:Tag "filmID"][:Out "name"][:Tag "name"][:Back "filmID"][:Out "type"][:All]] "http://localhost:64210/api/v1/query/gremlin")
```
This runs the query against Cayleys REST API as:
```
g.V().Has("name","Burt Reynolds").In("/film/performance/actor").In("/film/film/starring").Tag("filmID").Out("name").Tag("name").Back("filmID").Out("type").All()
```

Query also takes optional path/query arguments that are bound to vars in the query.
Paths/Queries take the form of:
```clojure
["varname" [["g"][:Morphism]...]]
```

Paths/Queries are referenced in the query by the string name that preceeds the path form. So, they are referenced with the "Follow", "FollowR", "Intersect", and "Union" verbs.
