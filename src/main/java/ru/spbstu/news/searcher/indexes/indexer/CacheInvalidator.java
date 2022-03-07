package ru.spbstu.news.searcher.indexes.indexer;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;


@Component
public class CacheInvalidator {

    private static final Logger logger = LoggerFactory.getLogger(CacheInvalidator.class);

    private final Cache cache;

    @Autowired
    public CacheInvalidator(@NotNull Cache cache) {
        this.cache = cache;
    }

    public boolean invalidate(@NotNull SearchIndexDocument searchIndexDocument) {
        Validate.notNull(searchIndexDocument);
        try {
            cache.invalidate(searchIndexDocument);
            return true;
        } catch (Exception e) {
            logger.error("Cannot invalidate cache on index new document: [{}]", searchIndexDocument, e);
            return false;
        }
    }

}
