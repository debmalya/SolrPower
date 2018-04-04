package org.deb.bbclive.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.solr.client.solrj.SolrServerException;
import org.deb.bbclive.context.AppContext;
import org.deb.bbclive.dao.SolrDao;
import org.deb.bbclive.model.NewsExtract;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class will crawl websites and retrieve text information.
 */
@Component
public class Crawler {

	private static long totalCall = 0;
	private static long successCall = 0;
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Crawler.class);
	@Autowired
	AppContext appContext;
	Set<String> alreadyProcessed = new HashSet<>();
	// private HtmlToPlainText formatter = new HtmlToPlainText();

	/**
	 * @param uriSetToCrawl
	 *            these URIs will be parsed and text will be extracted.
	 * @param solrDao
	 * @return
	 */
	public CompletableFuture<Set<String>> process(Set<String> uriSetToCrawl, SolrDao solrDao) {
		alreadyProcessed = Collections.synchronizedSet(alreadyProcessed);
		CompletableFuture<Set<String>> completableFuture = CompletableFuture.supplyAsync(() -> {
			uriSetToCrawl.forEach(e -> {
				String url = appContext.getBaseURL() + e;
				try {

					if (alreadyProcessed.add(url)) {
						totalCall++;
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Trying to process URL :" + url);
						}
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

								sb.append(content);
								sb.append(" ");
							}
						});

						try {
							solrDao.addNews(sb.toString(), "", url);
							successCall++;
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Processed URL :" + url);
							}
						} catch (Throwable e1) {
							LOGGER.error(
									"Total call :" + totalCall + " success :" + successCall
											+ " error occurred while processing URL :" + url + " " + e1.getMessage(),
									e1);
						}

					}
				} catch (IOException e1) {
					LOGGER.error("Total call :" + totalCall + " success :" + successCall
							+ " error occurred while processing URL :" + url + " " + e1.getMessage(), e1);
				}

			});
			return uriSetToCrawl;
		}).toCompletableFuture();

		return completableFuture;
	}

	/**
	 * @param uriSetToCrawl
	 *            these URIs will be parsed and text will be extracted.
	 * @param solrDao
	 * @return
	 */
	public CompletableFuture<Set<String>> processMultiple(Set<String> uriSetToCrawl, SolrDao solrDao) {
		alreadyProcessed = Collections.synchronizedSet(alreadyProcessed);
		List<NewsExtract> newsCollection = new ArrayList<>();

		CompletableFuture<Set<String>> completableFuture = CompletableFuture.supplyAsync(() -> {
			uriSetToCrawl.forEach(e -> {
				String url = appContext.getBaseURL() + e;
				try {

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

								sb.append(content);
								sb.append(" ");
							}
						});

						try {
							if (sb.toString().trim().length() > 0) {
								newsCollection.add(new NewsExtract("", sb.toString(), url));
								if (newsCollection.size() % 5 == 0) {
									solrDao.addLotsOfNews(newsCollection);
									newsCollection.clear();
								}
								successCall++;
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Processed URL :" + url);
								}
							}
						} catch (Throwable e1) {
							LOGGER.error(
									"Total call :" + totalCall + " success :" + successCall
											+ " error occurred while processing URL :" + url + " " + e1.getMessage(),
									e1);
						}

					}
				} catch (IOException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}

			});
			if (!newsCollection.isEmpty()) {
				try {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Going to insert :" + newsCollection);
					}
					solrDao.addLotsOfNews(newsCollection);
				} catch (IOException | SolrServerException e1) {
					LOGGER.error("Error while adding " + newsCollection + " ERR -> " + e1.getMessage(), e1);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("There is no more news");
				}
			}
			return uriSetToCrawl;
		}).toCompletableFuture();

		return completableFuture;
	}
}
