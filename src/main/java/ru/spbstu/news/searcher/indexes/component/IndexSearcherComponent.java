package ru.spbstu.news.searcher.indexes.component;

import org.apache.commons.math3.util.Pair;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.indexes.searcher.LuceneIndexSearcher;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexIllegalPartitions;
import ru.spbstu.news.searcher.indexes.searcher.Searcher;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@DependsOn("indexWriterComponent")
public class IndexSearcherComponent implements Searcher {

    private static final Logger logger = LoggerFactory.getLogger(IndexSearcherComponent.class);
    private static final Integer DEFAULT_SEARCH_DOCS_AMOUNT = 10;

    private final String indexDir;
    private final LuceneIndexSearcher[] luceneIndexSearchers;

    public IndexSearcherComponent(@Value("${indexer.partions.amount}") int partitions,
                                  @Value("${indexer.indexDir}") String indexDir) throws LuceneIndexIllegalPartitions {
        if (partitions <= 0) {
            throw new LuceneIndexIllegalPartitions("Number of partitions is less than 0");
        }
        this.indexDir = indexDir;
        this.luceneIndexSearchers = new LuceneIndexSearcher[partitions];
        for (int partition = 1; partition <= partitions; ++partitions) {
            luceneIndexSearchers[toIndex(partition)] = new LuceneIndexSearcher(String.valueOf(partition), partition);
        }
    }

    public static int toIndex(int partition) {
        return partition - 1;
    }

    @PostConstruct
    public void init() throws LuceneIndexingException {
        open(indexDir);
    }

    @Override
    public void open(@NotNull String indexDir) throws LuceneIndexingException {
        for (LuceneIndexSearcher luceneIndexSearcher : luceneIndexSearchers) {
            luceneIndexSearcher.open(indexDir);
        }
    }

    @Override
    public Pair<List<SearchIndexDocument>, Long> search(@NotNull Query query, @NotNull Sort sort, @Nullable Integer docsCount) throws LuceneOpenException {
        Set<SearchIndexDocument> allPartitionDocs = new HashSet<>();
        docsCount = docsCount == null ? DEFAULT_SEARCH_DOCS_AMOUNT : docsCount;
        long totalCount = 0;
        for (LuceneIndexSearcher luceneIndexSearcher : luceneIndexSearchers) {
            Pair<List<SearchIndexDocument>, Long> searchResult = luceneIndexSearcher.search(query, sort, docsCount);
            allPartitionDocs.addAll(searchResult.getKey());
            totalCount += searchResult.getValue();
        }
        return Pair.create(allPartitionDocs.stream()
                .limit(docsCount)
                .collect(Collectors.toList()),
                totalCount);
    }

    @NotNull
    public Pair<List<SearchIndexDocument>, Long> searchIndexDocuments(@Nullable String textQuery,
                                                                      @NotNull Integer docsCount) {
        Query query = SearchIndexDocumentConverter.createQuery(textQuery);
        if (query == null) {
            logger.warn("Provided query is null");
            return Pair.create(Collections.emptyList(), 0L);
        }
        try {
            return search(query, SearchIndexDocumentConverter.createSort(), docsCount);
        } catch (LuceneOpenException e) {
            logger.warn("Cannot find search index documents with text query: [{}], docsCount: [{}]",
                    textQuery, docsCount, e);
            return Pair.create(Collections.emptyList(), 0L);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        for (LuceneIndexSearcher luceneIndexSearcher : luceneIndexSearchers) {
            luceneIndexSearcher.close();
        }
    }
}
