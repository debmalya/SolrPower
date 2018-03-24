package org.deb.bbclive.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import javax.annotation.Resource;

@Configuration
@EnableSolrRepositories(basePackages={"org.deb.bbclive"})
public class SolrContext {
    static final String SOLR_HOST = "http://sg1lxped06:8983/solr";

    @Resource
    private Environment environment;



}
