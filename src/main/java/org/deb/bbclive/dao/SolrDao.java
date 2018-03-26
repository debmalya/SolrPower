package org.deb.bbclive.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.deb.bbclive.context.AppContext;
import org.deb.bbclive.model.NewsExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * While the UpdateResponse and QueryResponse interfaces that SolrJ provides are useful, it is often more convenient to work with domain-specific objects that can more easily be understood by your application.
 */

@Component
@Configuration
public class SolrDao {


    @Autowired
    AppContext appContext;

    @Autowired
    private SolrClient solrClient;

    /**
     * @param query  SOLR query (Sample query "*:*" )
     * @param fields fields (e.g. "id, name" )
     * @return retrieved documents.
     */
    public SolrDocumentList query(String query, String fields) throws IOException, SolrServerException {
        final Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("q", query);
        queryParamMap.put("fl", fields);
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        final QueryResponse response = solrClient.query(appContext.getCollectionName(), queryParams);
        final SolrDocumentList documents = response.getResults();
        return documents;
    }

    /**
     * Retrieve news extract.
     *
     * @param query  SOLR query (Sample query "*:*" )
     * @param fields fields (e.g. "id, name" )
     * @return retrieved documents.
     */
    public List<NewsExtract> getNewExtract(String query, String fields) throws IOException, SolrServerException {
        final Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("q", query);
        queryParamMap.put("fl", fields);
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        final QueryResponse response = solrClient.query(appContext.getCollectionName(), queryParams);
        final List<NewsExtract> newsExtractList = response.getBeans(NewsExtract.class);
        return newsExtractList;
    }

    /**
     * @param query              string
     * @param fieldNameList      field names.
     * @param numResultsToReturn number of rows to return.
     * @return SOLR query.
     */
    public SolrQuery createSolrQuery(String query, List<String> fieldNameList, int numResultsToReturn) {
        final SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addField("id");
        solrQuery.addField("name");
        solrQuery.setRows(numResultsToReturn);

        return solrQuery;
    }

    /**
     * This will add news extract to SOLR.
     * Under production circumstances, documents should be indexed in larger batches, instead of one at a time.
     *
     * @param news     extract.
     * @param entities related to the news.
     * @return update response
     */
    public UpdateResponse addNews(String news, String entities,String url) throws IOException, SolrServerException {
        final NewsExtract newsExtract = new NewsExtract(entities, news, url);

        final UpdateResponse updateResponse = solrClient.addBean(appContext.getCollectionName(), newsExtract);
        // Indexed documents must be committed
        solrClient.commit(appContext.getCollectionName());
        solrClient.optimize();
        return updateResponse;
    }
    
    /**
     * This will add news extract to SOLR.
     * Under production circumstances, documents should be indexed in larger batches, instead of one at a time.
     *
     * @param news     extract.
     * @param entities related to the news.
     * @return update response
     */
    public UpdateResponse addLotsOfNews(List<NewsExtract> newsList) throws IOException, SolrServerException {
        

        final UpdateResponse updateResponse = solrClient.addBeans(appContext.getCollectionName(), newsList);
        // Indexed documents must be committed
        solrClient.commit(appContext.getCollectionName());
        solrClient.optimize();
        return updateResponse;
    }

}
