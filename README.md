# About
How to use Solr ( 7.2.1 ) for information storing and retrieval.

# How to create and drop collection
* bin/solr create -c news-extract -s 2 -rf 2
* bin/solr delete -c news-extract

# How to run solr using docker
docker run --name my_solr -d -p 8983:8983 -t solr

# How to run jaeger for opentracing
* docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest

# Connection configuration
* In application.properties you can define what type of solr connection you need.
** (E.g. indexBased=false , cloudBased=false )

# How to run the application
* mvn spring-boot:run
* mvn spring-boot:run -Dserver.port=<port number>



# Solr commands
bin/solr start -e schemaless
bin/post -h
bin/post -c gettingstarted example/films/films.json

GET /collection/schema/fieldtypes
GET /collection/schema/fieldtypes/name
GET /collection/schema/dynamicfields
GET /collection/schema/dynamicfields/name
GET /collection/schema/copyfields
GET /collection/schema/name
GET /collection/schema/version
GET /collection/schema/uniquekey
GET /collection/schema/similarity

curl http://localhost:8983/solr/gettingstarted/schema/fieldtypes
curl http://localhost:8983/solr/gettingstarted/schema/dynamicfields
curl http://localhost:8983/solr/gettingstarted/schema/copyfields
curl http://localhost:8983/solr/gettingstarted/schema/name
curl http://localhost:8983/solr/gettingstarted/schema/version
curl http://localhost:8983/solr/gettingstarted/schema/uniquekey
curl http://localhost:8983/solr/gettingstarted/schema/similarity
curl http://host:8983/solr/mycollection/config -d '{"set-user-property":{"update.autoCreateFields":"false"}}'
curl 'http://localhost:8983/solr/my_collection/update?commit=true' --data-binary @example/exampledocs/books.csv -H 'Content-type:application/csv'