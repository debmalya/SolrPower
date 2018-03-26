package org.deb.bbclive.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.deb.bbclive.context.AppContext;
import org.deb.bbclive.crawler.Crawler;
import org.deb.bbclive.dao.SolrDao;
import org.deb.bbclive.model.DisplayableItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LiveController {

	@Autowired
	SolrDao solrDao;

	@Autowired
	Crawler crawler;

	@Autowired
	AppContext appContext;

	private static final Logger LOGGER = LoggerFactory.getLogger(LiveController.class);

	String[] arr = new String[] { "And we'll keep you signed in.",
			"You need one to watch live TV on any channel or device, and BBC programmes on iPlayer. It’s the law.",
			"By Greg O'Keeffe and Daryl Hammond", "All times stated are UK", "Thanks for joining us today.",
			"There's plenty to look forward to tonight: not least two ties for us to savour.",
			"Read more about that and all the other day's sporting stories", "#bbcfootball or 81111 on text",
			"The latest global news, sport, weather and documentaries", "Stories from around the world", "Email us at",
			"Send an SMS or MMS to", "Follow", "Run by the BBC and partners" };
	
	List<String> nonEntries = Arrays.asList(arr);
	
	private Set<String> linksToBeRetrieved = new HashSet<>();
	
	private List<DisplayableItem> displayableItemList;

	

	@RequestMapping("/")
	public String newsHeadLines(Model model) throws IOException {

//		if (displayableItemList == null) {
			displayableItemList = getHeadLinesWithLinks();
//		}
		model.addAttribute("newsList", displayableItemList);

		
		return "news_headings";
	}

	/**
	 * 
	 * @return list of display able item. Each display able item has text and a link to get details.
	 * @throws IOException
	 */
	private List<DisplayableItem> getHeadLinesWithLinks() throws IOException {
		List<DisplayableItem> displayableItemList = new ArrayList<>();

		for (String eachLink : appContext.getLinks()) {
			Document doc = Jsoup.connect(eachLink).get();

			Elements links = doc.select("a[href]");

			links.forEach(e -> {
				String link = e.attr("href");

				if (link.startsWith("/sport/football")) {
					String headLine = e.ownText().trim();
					long wordCount = headLine.split("\\s").length;

					if (headLine != null && headLine.length() > 0 && wordCount > 1) {
						linksToBeRetrieved.add(link);
						displayableItemList.add(new DisplayableItem(headLine, appContext.getBaseURL()+link));
					}

				}
			});
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total item :" + displayableItemList.size());
		}
		CompletableFuture<Set<String>> crawled_content = crawler.processMultiple(linksToBeRetrieved, solrDao);
		if (!crawled_content.isDone()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Still crawling...");
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Completed crawling...");
			}
		}
		return displayableItemList;
	}
}
