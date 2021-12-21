package ru.spbstu.news.searcher.indexes;

import org.apache.commons.codec.digest.MurmurHash2;
import org.jetbrains.annotations.NotNull;

public class IndexPartitioner {

    public static int getPartition(@NotNull SearchIndexDocument indexDocument,
                                   int partitionsAmount) {
        String fullText = indexDocument.getFullText();
        int hash = MurmurHash2.hash32(fullText);
        return (hash % partitionsAmount) + 1;
    }

}
