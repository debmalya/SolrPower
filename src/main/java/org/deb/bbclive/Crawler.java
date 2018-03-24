package org.deb.bbclive;

import org.apache.solr.client.solrj.SolrServerException;
import org.deb.bbclive.context.AppContext;
import org.deb.bbclive.dao.SolrDao;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * This class will crawl websites and retrieve text information.
 */
@Component
public class Crawler {

    private static long totalCall = 0;
    private static long successCall = 0;
    @Autowired
    AppContext appContext;
    Set<String> alreadyProcessed = new HashSet<>();
    private HtmlToPlainText formatter = new HtmlToPlainText();

    /**
     * @param uriSetToCrawl these URIs will be parsed and text will be extracted.
     * @param solrDao
     * @return
     */
    public CompletableFuture<Set<String>> process(Set<String> uriSetToCrawl, SolrDao solrDao) {
        alreadyProcessed = Collections.synchronizedSet(alreadyProcessed);
        CompletableFuture<Set<String>> completableFuture = CompletableFuture.supplyAsync(() ->
        {
            uriSetToCrawl.forEach(e -> {
                String url = appContext.getBaseURL() + e;
                try {

//                    System.out.println("===========");
//                    System.out.println(url);
//                    System.out.println("============");
                    if (alreadyProcessed.add(url)) {
                        totalCall++;
                        Document doc = Jsoup.connect(url).get();
                        Elements paragraphs = doc.select("p");
                        HtmlToPlainText plainText = new HtmlToPlainText();

                        StringBuilder sb = new StringBuilder();
                        paragraphs.parallelStream().forEach(p -> {
                            String content = plainText.getPlainText(p).trim();

                            if (content.length() > 0) {
                                for (String toBeReplaced : appContext.getIgnoreList()) {
                                    content = content.replace(toBeReplaced, "");
                                }
                                content = content.replace("\\n", "");
//                                System.out.println(content);

                                sb.append(content);
                                sb.append(" ");
                            }
                        });

                        try {
                            solrDao.addNews(sb.toString(), "", url);
                            successCall++;
                        } catch (SolrServerException e1) {
                            System.err.println("Total call :" + totalCall + " success :" + successCall);
                            e1.printStackTrace();
                        }


                    }
                } catch (IOException e1) {
                    System.err.println("For url :" + url + " total call :" + totalCall + " success :" + successCall);
                    e1.printStackTrace();
                }

            });
            return uriSetToCrawl;
        }).toCompletableFuture();
        if (completableFuture.isDone()) {
            System.out.println("...completed crawling");
        }
        return completableFuture;
    }
}
