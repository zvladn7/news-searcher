package ru.spbstu.news.searcher.indexes;

import org.apache.commons.lang3.Validate;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.BytesRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchIndexDocumentConverter {

    private static final Logger logger = LoggerFactory.getLogger(SearchIndexDocumentConverter.class);

    private static final String DATABASE_ID_FIELD = "database_id";
    private static final String FULL_TEXT_FIELD = "full_text";

    private static final Analyzer analyzer = AnalyzerProvider.provide();

    @Nullable
    public static Query createQueryFullText(@Nullable String fullText) throws ParseException {
        if (fullText == null) {
            return null;
        }
        return new QueryParser(FULL_TEXT_FIELD, analyzer).parse(fullText);
    }

    @Nullable
    public static Query createQueryDatabaseId(@Nullable Long databaseId) throws ParseException {
        String databaseIdString = String.valueOf(databaseId);
        if (databaseIdString == null) {
            return null;
        }
        return new QueryParser(DATABASE_ID_FIELD, analyzer).parse(databaseIdString);
    }

    public static Sort createSort() {
        SortField sortField = new SortField(FULL_TEXT_FIELD, SortField.Type.STRING_VAL, false);
        return new Sort(sortField);
    }

    public static Term createDatabaseIdTerm(@NotNull Long databaseId) {
        String databaseIdString = String.valueOf(databaseId);
        return new Term(DATABASE_ID_FIELD, databaseIdString);
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
        document.add(new StoredField(DATABASE_ID_FIELD, databaseId));
        document.add(new TextField(FULL_TEXT_FIELD, fullText, Field.Store.YES));
        document.add(new SortedDocValuesField(FULL_TEXT_FIELD, new BytesRef(fullText)));

        return document;
    }

    @Nullable
    public static Document convertInMemory(@NotNull SearchIndexDocument searchIndexDocument) {
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
        document.add(new TextField(DATABASE_ID_FIELD, String.valueOf(databaseId), Field.Store.YES));
        document.add(new TextField(FULL_TEXT_FIELD, fullText, Field.Store.YES));

        return document;
    }

    public static SearchIndexDocument convertFromMemory(@NotNull Document document) {
        Validate.notNull(document);
        IndexableField databaseField = document.getField(DATABASE_ID_FIELD);
        String databaseIdString =  databaseField.stringValue();
        Long databaseId = Long.parseLong(databaseIdString);
        IndexableField fullTextField = document.getField(FULL_TEXT_FIELD);
        String fullText = fullTextField.stringValue();

        return new SearchIndexDocument(databaseId, fullText);
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
