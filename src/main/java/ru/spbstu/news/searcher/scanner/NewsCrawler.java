package ru.spbstu.news.searcher.scanner;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.service.SearchResultService;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewsCrawler extends WebCrawler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(NewsCrawler.class);
    private static final Pattern FILE_ENDING_EXCLUSION_PATTERN = Pattern.compile(".*(\\.(" +
            "css|js" +
            "|bmp|gif|jpe?g|JPE?G|png|tiff?|ico|nef|raw" +
            "|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g" +
            "|avi|mov|mpeg|ram|m4v|wmv|rm|smil" +
            "|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx" +
            "|swf" +
            "|zip|rar|gz|bz2|7z|bin" +
            "|xml|txt|java|c|cpp|exe" +
            "))$");

    private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|jpg|png|tiff?))$");

    @NotNull
    private final SearchResultService searchResultService;

    public NewsCrawler(@NotNull final SearchResultService searchResultService) {
        this.searchResultService = searchResultService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final String href = url.getURL().toLowerCase();
        for (String domain : CrawlerConfig.resources) {
            if (href.startsWith(domain)) {
                return !FILE_ENDING_EXCLUSION_PATTERN.matcher(href).matches();
            }
        }
        return false;
    }

    @Override
    public void visit(Page page) {
        final String url = page.getWebURL().getURL();
        logger.info("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData && !CrawlerConfig.resources.contains(url)) {
            final HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            final Document doc = Jsoup.parse(htmlParseData.getHtml());
            final String text = getText(doc);
            if (text.isEmpty()) {
                return;
            }
            final Set<WebURL> urls = htmlParseData.getOutgoingUrls();
            logger.info("Text length: " + text.length());
            logger.info("Number of outgoing links: " + urls.size());
            try {
                searchResultService.index(new ItemToIndex(url, filterImages(urls), text.replaceAll("\\s+", " ")));
            } catch (LuceneOpenException e) {
                logger.error("lucene open ex", e);
            }
        }
    }

    private String getText(Document doc) {
        String result = "";
        final Elements mailNewsElement = doc.getElementsByClass("article__intro");
        final Elements mailCardElement = doc.getElementsByClass("wrapper cover__inner");
        final Elements riaNewsElement = doc.getElementsByClass("article__header");
        final Element ramblerNewsElement = doc.getElementById("bigColumn");
        if (!mailCardElement.text().isEmpty()) {
            result = mailCardElement.text();
        } else if (!mailNewsElement.text().isEmpty()) {
            result = mailNewsElement.text();
        } else if (ramblerNewsElement != null && !ramblerNewsElement.text().isEmpty()) {
            result = ramblerNewsElement.text();
        } else if (!riaNewsElement.text().isEmpty()) {
            result = riaNewsElement.text();
        }

        return result;
    }

    private List<String> filterImages(@NotNull final Set<WebURL> urls) {
        return urls.stream()
                .filter((url) -> {
                    final String href = url.getURL().toLowerCase();
                    if (!imgPatterns.matcher(href).matches()
                            || (href.contains("logo") || href.contains("icon"))) {
                        return false;
                    }
                    try {
                        URL imageUrl = new URL(href);
                        URLConnection conn = imageUrl.openConnection();
                        InputStream in = conn.getInputStream();
                        if (in.available() >= (10 * 1024)) {
                            return true;
                        }
                    } catch (Exception ex) {
                        return false;
                    }
                    return false;
                })
                .map(WebURL::toString)
                .collect(Collectors.toList());
    }
}
