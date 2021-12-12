package ru.spbstu.news.searcher.scanner;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.service.SearchResultService;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewsCrawler extends WebCrawler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(NewsCrawler.class);
    private static final Pattern FILE_ENDING_EXCLUSION_PATTERN = Pattern.compile(".*(\\.(" +
            "css|js" +
            "|bmp|gif|jpe?g|JPE?G|png|nef|raw" +
            "|xml|txt|java|c|cpp|exe" +
            "))$");

    @NotNull
    private final SearchResultService searchResultService;

    public NewsCrawler(@NotNull final SearchResultService searchResultService) {
        this.searchResultService = searchResultService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final String href = url.getURL().toLowerCase();
        return !FILE_ENDING_EXCLUSION_PATTERN.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        final String url = page.getWebURL().getURL();
        logger.info("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData && !CrawlerConfig.resources.contains(url)) {
            final HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            final String text = htmlParseData.getText();
            final Set<WebURL> urls = htmlParseData.getOutgoingUrls();
            logger.info("Text length: " + text.length());
            logger.info("Number of outgoing links: " + urls.size());
            try {
                searchResultService.index(new ItemToIndex(url, filterImages(urls), text.replaceAll("\\s", " ")));
            } catch (LuceneOpenException e) {
                logger.error("lucene open ex", e);
            }
        }
    }

    private List<String> filterImages(@NotNull final Set<WebURL> urls) {
        return urls.stream()
                .filter((url) -> {
                    if (url.toString().contains("logo") || url.toString().contains("icon")) {
                        return false;
                    }
                    String format = StringUtils.substringAfterLast(url.getPath(), ".");
                    return format.equals("png") || format.equals("jpg") || format.equals("jpeg");
                })
                .map(WebURL::toString)
                .collect(Collectors.toList());
    }
}
