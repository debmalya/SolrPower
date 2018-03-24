package org.deb.bbclive.model;

import org.apache.solr.client.solrj.beans.Field;

public class NewsExtract {
    @Field
    String url;
    @Field
    String entity;
    @Field
    String newsExtract;

    public NewsExtract(String entity, String newsExtract,String url) {
        this.entity = entity;
        this.newsExtract = newsExtract;
        this.url = url;
    }

    public NewsExtract() {
    }

    @Override
    public String toString() {
        return "NewsExtract{" +
                "url='" + url + '\'' +
                ", entity='" + entity + '\'' +
                ", newsExtract='" + newsExtract + '\'' +
                '}';
    }
}
