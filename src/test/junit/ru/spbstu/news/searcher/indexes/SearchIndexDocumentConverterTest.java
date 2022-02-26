package ru.spbstu.news.searcher.indexes;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SearchIndexDocumentConverterTest {

    private static final String FULL_TEXT = "футбол";
    private static final long DATABASE_ID = 123L;

    @Test
    public void createQueryFullText_fullTextNull() throws ParseException {
        Assert.assertEquals(null, SearchIndexDocumentConverter.createQueryFullText(null));
    }

    @Test
    public void createQueryFullText_fullTextNormal() throws ParseException {
        Query query = SearchIndexDocumentConverter.createQueryFullText(FULL_TEXT);
        String stringQuery = query.toString();
        String[] splitedQuery = stringQuery.split(":");
        Assert.assertEquals(SearchIndexDocumentConverter.FULL_TEXT_FIELD, splitedQuery[0]);
        Assert.assertEquals(FULL_TEXT, splitedQuery[1]);
    }

    @Test
    public void createQueryDatabaseId_DatabaseIdNull() throws ParseException {
        Assert.assertEquals(null, SearchIndexDocumentConverter.createQueryDatabaseId(null));
    }

    @Test
    public void createQueryDatabaseId_fullTextNormal() throws ParseException {
        Query query = SearchIndexDocumentConverter.createQueryDatabaseId(DATABASE_ID);
        String stringQuery = query.toString();
        String[] splitedQuery = stringQuery.split(":");
        Assert.assertEquals(SearchIndexDocumentConverter.DATABASE_ID_FIELD, splitedQuery[0]);
        Assert.assertEquals(DATABASE_ID, Long.parseLong(splitedQuery[1]));
    }

    @Test(expected = NullPointerException.class)
    public void createDatabaseIdTerm_DatabaseIdNull() {
        SearchIndexDocumentConverter.createDatabaseIdTerm(null);
    }

    @Test
    public void createDatabaseIdTerm_DatabaseIdNormal() {
        long databaseId = 123L;
        Term term = SearchIndexDocumentConverter.createDatabaseIdTerm(databaseId);
        String termString = term.toString();
        String[] termStringSplited = termString.split(":");
        Assert.assertEquals(SearchIndexDocumentConverter.DATABASE_ID_FIELD, termStringSplited[0]);
        Assert.assertEquals(databaseId, Long.parseLong(termStringSplited[1]));
    }

    @Test(expected = NullPointerException.class)
    public void convert_documentNull() {
        SearchIndexDocument document = null;
        SearchIndexDocumentConverter.convert(document);
    }

    @Test
    public void convert_documentFullTextNull() {
        SearchIndexDocument document = new SearchIndexDocument(DATABASE_ID, null);
        Assert.assertEquals(null, SearchIndexDocumentConverter.convert(document));
    }

    @Test
    public void convert_documentDatabaseIdNull() {
        SearchIndexDocument document = new SearchIndexDocument(null, FULL_TEXT);
        Assert.assertEquals(null, SearchIndexDocumentConverter.convert(document));
    }

    @Test
    public void convert_documentNormal() {
        SearchIndexDocument searchIndexDocument = new SearchIndexDocument(DATABASE_ID, FULL_TEXT);
        Document document = SearchIndexDocumentConverter.convert(searchIndexDocument);
        IndexableField fullTextField = document.getField(SearchIndexDocumentConverter.FULL_TEXT_FIELD);
        Assert.assertTrue(fullTextField.fieldType().stored());
        Assert.assertEquals(FULL_TEXT, fullTextField.stringValue());
        Assert.assertSame(fullTextField.getClass(), TextField.class);

        IndexableField databaseIdField = document.getField(SearchIndexDocumentConverter.DATABASE_ID_FIELD);
        Assert.assertTrue(databaseIdField.fieldType().stored());
        Assert.assertEquals(DATABASE_ID, Long.parseLong(databaseIdField.stringValue()));
    }

    @Test(expected = NullPointerException.class)
    public void convertInMemory_documentNull() {
        SearchIndexDocument document = null;
        SearchIndexDocumentConverter.convertInMemory(document);
    }

    @Test
    public void convertInMemory_documentFullTextNull() {
        SearchIndexDocument document = new SearchIndexDocument(DATABASE_ID, null);
        Assert.assertEquals(null, SearchIndexDocumentConverter.convertInMemory(document));
    }

    @Test
    public void convertInMemory_documentDatabaseIdNull() {
        SearchIndexDocument document = new SearchIndexDocument(null, FULL_TEXT);
        Assert.assertEquals(null, SearchIndexDocumentConverter.convertInMemory(document));
    }

    @Test
    public void convertInMemory_documentNormal() {
        SearchIndexDocument searchIndexDocument = new SearchIndexDocument(DATABASE_ID, FULL_TEXT);
        Document document = SearchIndexDocumentConverter.convertInMemory(searchIndexDocument);
        IndexableField fullTextField = document.getField(SearchIndexDocumentConverter.FULL_TEXT_FIELD);
        Assert.assertTrue(fullTextField.fieldType().stored());
        Assert.assertEquals(FULL_TEXT, fullTextField.stringValue());
        Assert.assertSame(fullTextField.getClass(), TextField.class);

        IndexableField databaseIdField = document.getField(SearchIndexDocumentConverter.DATABASE_ID_FIELD);
        Assert.assertTrue(databaseIdField.fieldType().stored());
        Assert.assertEquals(DATABASE_ID, Long.parseLong(databaseIdField.stringValue()));
    }

    @Test(expected = NullPointerException.class)
    public void convertFromMemory_documentNull(){
        SearchIndexDocumentConverter.convertFromMemory(null);
    }

    @Test(expected = NullPointerException.class)
    public void convertBack_documentNull(){
        Document document = null;
        SearchIndexDocumentConverter.convert(document);
    }
}
