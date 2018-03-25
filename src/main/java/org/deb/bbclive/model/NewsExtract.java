package org.deb.bbclive.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

public class NewsExtract {
	@Field
	@Id
	String id;
    @Field
    String url;
    @Field
    String entity;
    @Field
    String newsExtract;
    @Field
    Date publicationDate;

    /**
     * This will be called when new publication date is not known.
     * System date will be used as publication date
     * @param entity news related to which entity
     * @param newsExtract news details
     * @param url retrieved from which URL
     */
	public NewsExtract(String entity, String newsExtract,String url) {
        this.entity = entity;
        this.newsExtract = newsExtract;
        this.url = url;
        this.publicationDate=new Date();
        this.id=url;
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
