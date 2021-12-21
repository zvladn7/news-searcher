package ru.spbstu.news.searcher.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.RAMDirectory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.component.InMemoryIndexComponent;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TitleExtractor {

    private static final Logger logger = LoggerFactory.getLogger(TitleExtractor.class);

    private final static Integer LENGTH_TO_SPLIT_FULL_TEXT = 30;
    private final static Integer DEFAULT_LENGTH_OF_TITLE_FROM_START = 20;

    private final InMemoryIndexComponent inMemoryIndexComponent;

    @Autowired
    public TitleExtractor(InMemoryIndexComponent inMemoryIndexComponent) {
        this.inMemoryIndexComponent = inMemoryIndexComponent;
    }


    public String getTitleFromFullText(@NotNull String fullText,
                                       @NotNull Long databaseId,
                                       @NotNull String query) {
        Validate.notNull(fullText);
        Validate.notNull(databaseId);
        Validate.notNull(query);
        try {
            Iterable<String> chunks = getChunks(fullText);
            List<SearchIndexDocument> documentsToStoreInMemoryIndex = new ArrayList<>();
            for (String chunk : chunks) {
                String fullChunk = databaseId + ": " + chunk;
                documentsToStoreInMemoryIndex.add(new SearchIndexDocument(databaseId, fullChunk));
            }
            RAMDirectory directory = inMemoryIndexComponent.index(documentsToStoreInMemoryIndex);
            Document document =
                    inMemoryIndexComponent.searchIndex(SearchIndexDocumentConverter.createQueryFullText(query), directory);
            if (document == null) {
                logger.warn("Document is null after search for title from text for \n" +
                        "fullText: [{}],\n" +
                        "database: [{}],\n" +
                        "query: [{}]", fullText, databaseId, query);
                return defaultResult(fullText);
            }
            SearchIndexDocument searchIndexDocument = SearchIndexDocumentConverter.convertFromMemory(document);
            inMemoryIndexComponent.deleteByDatabaseId(databaseId, directory);
            String title = searchIndexDocument.getFullText();
            return title.substring(title.indexOf(":") + 1);
        } catch (ParseException e) {
            logger.warn("Cannot parse string query to lucene query \n" +
                    "fullText: [{}],\n" +
                    "database: [{}],\n" +
                    "query: [{}]", fullText, databaseId, query, e);
        }
        return defaultResult(fullText);
    }

    private String defaultResult(@NotNull String fullText) {
        return fullText.substring(0, Math.min(fullText.length(), DEFAULT_LENGTH_OF_TITLE_FROM_START));
    }

    @NotNull
    private List<String> getChunks(@NotNull String fullText) {
        String[] split = fullText.split(" ");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String next : split) {
            current.append(next).append(" ");
            if (current.length() > LENGTH_TO_SPLIT_FULL_TEXT) {
                chunks.add(current.toString());
                current = new StringBuilder();
            }
        }
        if (StringUtils.isNotBlank(current.toString())) {
            chunks.add(current.toString());
        }
        return chunks;
    }


}
