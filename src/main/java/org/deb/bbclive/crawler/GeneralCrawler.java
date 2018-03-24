package org.deb.bbclive.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GeneralCrawler {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            Document doc = Jsoup.connect(args[0]).get();

            try (PrintWriter printWriter = new PrintWriter(new FileWriter("Extract.txt"))) {
                printWriter.println(doc.text());
            }

            // get all hyperlinks
            Elements links = doc.select("a[href]");
            try (PrintWriter printWriter = new PrintWriter(new FileWriter("Links.txt"))) {
                links.forEach(l->{
                    printWriter.println(l.text()+ " link " + l.attr("href"));
                });
            }
        } else {
            System.err.println("Expected parameter of this program is an URL");
        }
    }
}
