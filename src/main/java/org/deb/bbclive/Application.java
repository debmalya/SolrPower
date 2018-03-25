package org.deb.bbclive;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.deb.bbclive.context.AppContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.uber.jaeger.samplers.ProbabilisticSampler;

@SpringBootApplication
@ComponentScan("org.deb.bbclive")
@EntityScan("org.deb.bbclive.model")
public class Application {

    /**
     * SOLR baseURL to connect.
     */
    @Value("${solr.baseURL:http://localhost:8983/solr}")
    private String baseURL;

    /**
     * Connection time out, otherwise it will use OS default value.
     */
    @Value("${solr.connectionTimeout:10000}")
    private int connectionTimeout;

    /**
     * Socket time out, otherwise it will use OS default value.
     */
    @Value("${solr.socketTimeout:60000}")
    private int socketTimeout;


    /**
     * Collection name to be used for searching.
     */
    @Value("${solr.collectionName:news-extract}")
    private String collectionName;

    /**
     * Whether index based or not.
     */
    @Value("${indexBased:false}")
    private boolean indexBased;

    /**
     * If concurrent SOLR connection,
     * then what will be the queue size.
     */
    @Value("${queueSize:100}")
    private int queueSize;


    @Value("${cloudBased:false}")
    private boolean cloudBased;
    /**
     * Links to be crawled.
     */
    @Value("${links:http://www.bbc.com/sport/football}")
    private String links;

    /**
     * Filter to be applied on the links for recursive crawling
     */
    @Value("${filter:sport/football}")
    private String filter;

    @Value("${crawl.baseURL:http://www.bbc.com/}")
    private String crawlBaseURL;

    @Value("${calais.api.token}")
    private String calaisApiToken;

    @Value("${ignore.values}")
    private String ignoreValues;

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    @Bean
    public io.opentracing.Tracer tracer() {
        return new com.uber.jaeger.Configuration(
                "news-extract",
                new com.uber.jaeger.Configuration.SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
                new com.uber.jaeger.Configuration.ReporterConfiguration(
                        true,  // logSpans
                        "localhost",
                        5775,
                        1000,   // flush interval in milliseconds
                        10000)  // max buffered Spans
        ).getTracer();
    }




    @Bean
    public SolrClient solrClient() {

        SolrClient solrClient = null;
        // If index based then ConcurrentUpdateSolrClient instance will be created.
        if (indexBased) {
            ConcurrentUpdateSolrClient.Builder builder = new ConcurrentUpdateSolrClient.Builder(baseURL);
            solrClient = builder.withQueueSize(queueSize).build();
        } else if (cloudBased) {
            // If cloud based then CloudSolrClient instance will be created.
            CloudSolrClient.Builder clBuilder = new CloudSolrClient.Builder();
            CloudSolrClient cloudSolrClient = clBuilder.withSolrUrl(baseURL).build();
            cloudSolrClient.setDefaultCollection(collectionName);
            cloudSolrClient.setIdField("newsExtract");
            solrClient = cloudSolrClient;


        } else {
            solrClient = new HttpSolrClient.Builder(baseURL)
                    .withConnectionTimeout(connectionTimeout)
                    .withSocketTimeout(socketTimeout)
                    .build();
        }


        return solrClient;
    }

    @Bean
    public AppContext appContext() {

        AppContext appContext = new AppContext(links.split(";"), collectionName, filter, crawlBaseURL);
        if (calaisApiToken != null) {
            appContext.setCalaisAPItoken(calaisApiToken);
        }
        if (ignoreValues != null) {
            appContext.setIgnoreList(ignoreValues.split(";"));
        }
        return appContext;
    }
}
