# About
How to use Solr ( 7.2.1 ) for information storing and retrieval.

# How to run solr using docker
* docker run --name my_solr -d -p 8983:8983 -t solr
* docker exec -it my_solr solr create_core -c gettingstarted
* docker exec -it my_solr post -c gettingstarted example/exampledocs/manufacturers.xml

# How to create and drop collection in solr cloud
* bin/solr create -c news-extract -s 2 -rf 2
* bin/solr delete -c news-extract


# How to run jaeger for opentracing using docker
* docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest

# How to run the application
mvn spring-boot:run
mvn spring-boot:run -Dserver.port=<port number>

# Solr commands
* bin/solr start -e schemaless

* GET /collection/schema/fieldtypes
* GET /collection/schema/fieldtypes/name
* GET /collection/schema/dynamicfields
* GET /collection/schema/dynamicfields/name
* GET /collection/schema/copyfields
* GET /collection/schema/name
* GET /collection/schema/version
* GET /collection/schema/uniquekey
* GET /collection/schema/similarity

* curl http://localhost:8983/solr/gettingstarted/schema/fieldtypes
* curl http://localhost:8983/solr/gettingstarted/schema/dynamicfields
* curl http://localhost:8983/solr/gettingstarted/schema/copyfields
* curl http://localhost:8983/solr/gettingstarted/schema/name
* curl http://localhost:8983/solr/gettingstarted/schema/version
* curl http://localhost:8983/solr/gettingstarted/schema/uniquekey
* curl http://localhost:8983/solr/gettingstarted/schema/similarity
* curl http://host:8983/solr/mycollection/config -d '{"set-user-property":{"update.autoCreateFields":"false"}}'

## Solr queries
* http://localhost:8983/solr/techproducts/select?q=id:SP2514N&wt=xml
* http://localhost:8983/solr/techproducts/select?q=id:SP2514N&fl=id+name&wt=xml
