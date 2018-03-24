package org.deb.bbclive.controller;

import org.deb.bbclive.context.AppContext;
import org.deb.bbclive.crawler.Crawler;
import org.deb.bbclive.dao.SolrDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Controller
public class LiveController {

    @Autowired
    SolrDao solrDao;

    @Autowired
    Crawler crawler;

    @Autowired
    AppContext appContext;
    String[] arr = new String[]{"And we'll keep you signed in.",
            "You need one to watch live TV on any channel or device, and BBC programmes on iPlayer. It’s the law.",
            "By Greg O'Keeffe and Daryl Hammond", "All times stated are UK",
            "Thanks for joining us today.",
            "There's plenty to look forward to tonight: not least two ties for us to savour.",
            "Read more about that and all the other day's sporting stories",
            "#bbcfootball or 81111 on text",
            "The latest global news, sport, weather and documentaries", "Stories from around the world",
            "Email us at",
            "Send an SMS or MMS to",
            "Follow",
            "Run by the BBC and partners"};
    List<String> nonEntries = Arrays.asList(arr);
    private Set<String> linksToBeRetrieved = new HashSet<>();

    @RequestMapping("/")
    public String newsHeadLines(Model model) throws IOException {
        List<String> newsList = new ArrayList<>();



        for (String eachLink : appContext.getLinks()) {
            Document doc = Jsoup.connect(eachLink).get();
//            doc.select("p").forEach(e -> {
//                String text = e.ownText().trim();
//                if (!"".equals(text) && !nonEntries.contains(text)) {
//                    newsList.add(e.ownText());
//                }
//
//                List<DataNode> dataNodeList = e.dataNodes();
//                Elements children = e.children();
//                String data = e.data();
//                String id = e.id();
//                String href = e.attr("href");
//                String img = e.attr("img");
//            });

            Elements links = doc.select("a[href]");
            links.forEach(e -> {
                String link = e.attr("href");
                if (link.startsWith("/sport/football")) {
                    String headLine = e.ownText().trim();
                    long wordCount = headLine.split("\\s").length;
                    if (headLine != null && headLine.length() > 0 && wordCount > 1)
                        newsList.add(headLine);
                    linksToBeRetrieved.add(e.attr("href"));
                }
            });
        }
        System.out.println("Total item :" + newsList.size());
        model.addAttribute("newsList", newsList);

        CompletableFuture<Set<String>> crawled_content = crawler.process(linksToBeRetrieved, solrDao);
        if (!crawled_content.isDone()){
            System.out.println("Still crawling...");
        }else{
            System.out.println("Completed crawling...");
        }
        return "news_headings";
    }
}
