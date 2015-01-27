# MiniTP #

This is an example project that shows how one can create a simple trading platform with Ohua.

## Installation ##
This project has the following dependencies:

* Java 1.8
* Leiningen (http://leiningen.org/)

It is enough to install Leiningen with the 4 little steps listed here: [http://leiningen.org/](http://leiningen.org/). Leiningen will download and install Clojure (1.6) and Ohua automatically.

## Usage ##
Start each of the following tests in a seperate shell:

```
lein test :only minitp.trader-test
lein test :only minitp.request-gateway-test
lein test :only minitp.provider-gateway-test
lein test :only minitp.fix-acceptor-test
```