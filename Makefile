.PHONY: clean aot jar tag outdated install deploy tree repl

clean:
	rm -rf target
	rm -rf classes

aot:
	mkdir classes

jar: clean aot tag
	clojure -A:jar

outdated:
	clojure -M:outdated

tag:
	clojure -A:tag

install: jar
	clojure -A:install

deploy: jar
	clojure -A:deploy

tree:
	mvn dependency:tree

with-ui:
	clojure -M:with-ui

repl: with-ui
	clojure -A:dev -A:repl

