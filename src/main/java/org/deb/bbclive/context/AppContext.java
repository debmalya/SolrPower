package org.deb.bbclive.context;

public class AppContext {
    /**
     * Links to be searched.
     */
    private String[] links;

    /**
     * Name of SOLR collection.
     */
    private String collectionName;

    /**
     * URI filtering criteria.
     */
    private String filter;
    /**
     * base URL to crawl consequently
     */
    private String baseURL;

    /**
     * Token to call calais API.
     */
    private String calaisAPItoken;

    /**
     * String to ignore from the news extract.
     */
    private String[] ignoreList;

    public AppContext(String[] links, String collectionName, String filter, String baseURL) {
        this.links = links;
        this.collectionName = collectionName;
        this.filter = filter;
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCalaisAPItoken() {
        return calaisAPItoken;
    }

    public void setCalaisAPItoken(String calaisAPItoken) {
        this.calaisAPItoken = calaisAPItoken;
    }

    public String[] getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(String[] ignoreList) {
        this.ignoreList = ignoreList;
    }
}
