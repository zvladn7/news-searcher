package ru.spbstu.news.searcher.indexes;

import org.apache.commons.lang3.Validate;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchIndexDocumentConverter {

    private static final Logger logger = LoggerFactory.getLogger(SearchIndexDocumentConverter.class);

    private static final String DATABASE_ID_FIELD = "database_id";
    private static final String FULL_TEXT_FIELD = "full_text";

    @Nullable
    public static Query createQuery(@Nullable String query) {
        if (query == null) {
            return null;
        }
        Term term = new Term(FULL_TEXT_FIELD, query);
        return new TermQuery(term);
    }

    public static Sort createSort() {
        SortField sortField = new SortField(FULL_TEXT_FIELD, SortField.Type.STRING_VAL, false);
        return new Sort(sortField);
    }

    @Nullable
    public static Document convert(@NotNull SearchIndexDocument searchIndexDocument) {
        Validate.notNull(searchIndexDocument);
        String fullText = searchIndexDocument.getFullText();
        Long databaseId = searchIndexDocument.getDatabaseId();
        if (fullText == null) {
            logger.warn("SearchIndexDocument has null text, [{}]", searchIndexDocument);
            return null;
        }
        if (databaseId == null) {
            logger.warn("SearchIndexDocument has null databaseId, [{}]", searchIndexDocument);
            return null;
        }
        Document document = new Document();
        document.add(new NumericDocValuesField(DATABASE_ID_FIELD, databaseId));
        document.add(new TextField(FULL_TEXT_FIELD, fullText, Field.Store.YES));

        return document;
    }

    public static SearchIndexDocument convert(@NotNull Document document) {
        Validate.notNull(document);
        IndexableField databaseField = document.getField(DATABASE_ID_FIELD);
        Long databaseId = (Long) databaseField.numericValue();
        IndexableField fullTextField = document.getField(FULL_TEXT_FIELD);
        String fullText = fullTextField.stringValue();

        return new SearchIndexDocument(databaseId, fullText);
    }

}
