package ru.spbstu.news.searcher.indexes;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IndexPartitionerTest {

    private static final int PARTITIONS_AMOUNT = 1;
    private static final long DATABASE_ID = 123L;
    private static final String FULL_TEXT = "full_text";

    @Test(expected = NullPointerException.class)
    public void getPartition_NullIndexDocument() {
        IndexPartitioner.getPartition(null, PARTITIONS_AMOUNT);
    }

    @Test
    public void getPartition_Normal() {
        SearchIndexDocument document = new SearchIndexDocument(DATABASE_ID, FULL_TEXT);
        int partition = IndexPartitioner.getPartition(document, PARTITIONS_AMOUNT);
        Assert.assertThat(partition, Matchers.greaterThanOrEqualTo(0));
        Assert.assertThat(partition, Matchers.lessThanOrEqualTo(1));
    }

}
