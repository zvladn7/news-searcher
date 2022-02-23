package ru.spbstu.news.searcher.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.component.InMemoryIndexComponent;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TitleExtractorTest {

    private static final String FULL_TEXT = "Футбольный клуб зенит попал в топ европейский команд!";
    private static final Long DATABASE_ID = 123L;
    private static final String QUERY = "клуб";

    @Mock
    private InMemoryIndexComponent inMemoryIndexComponent;

    @Mock
    private RAMDirectory directory;

    @Mock
    private Document document;

    @Mock
    private IndexableField databaseField;

    @Mock
    private IndexableField fullTextField;

    private TitleExtractor titleExtractor;

    @Before
    public void setUp() {
        titleExtractor = new TitleExtractor(inMemoryIndexComponent);
    }

    @Test(expected = NullPointerException.class)
    public void getTitleFromFullText_FullTextNull() {
        titleExtractor.getTitleFromFullText(null, DATABASE_ID, QUERY);
    }
    @Test(expected = NullPointerException.class)
    public void getTitleFromFullText_DatabaseIdNull() {
        titleExtractor.getTitleFromFullText(FULL_TEXT, null, QUERY);
    }

    @Test(expected = NullPointerException.class)
    public void getTitleFromFullText_QueryNull() {
        titleExtractor.getTitleFromFullText(FULL_TEXT, DATABASE_ID, null);
    }

    @Test(expected = NullPointerException.class)
    public void getChunks_FullTextNull() {
        titleExtractor.getChunks(null);
    }

    @Test
    public void getChunks_NotLongFullText() {
        String fullText = "За национальную команду России выступили Евгения Крупицкая, " +
                "Дарья Непряева, Анна Кожинова и Елизавета Бекишева.";
        List<String> chunks = titleExtractor.getChunks(fullText);
        Assert.assertEquals(
                (int) Math.ceil((double) fullText.length() / TitleExtractor.LENGTH_TO_SPLIT_FULL_TEXT),
                chunks.size());
    }

    @Test
    public void getChunk_NoSpaces() {
        String noSpacesFullText = "ЗанациональнуюкомандуРоссиивыступилиЕвгенияКрупицкаяДарьяНепряева,АннаКожиноваиЕлизаветаБекишева.";
        List<String> chunks = titleExtractor.getChunks(noSpacesFullText);
        Assert.assertEquals(1, chunks.size());
    }

    @Test
    public void getChunk_OnlySpaces() {
        String onlySpacesFullText = "                               ";
        List<String> chunks = titleExtractor.getChunks(onlySpacesFullText);
        Assert.assertEquals(0, chunks.size());
    }

    @Test(expected = NullPointerException.class)
    public void defaultResult_FullTextNull() {
        titleExtractor.defaultResult(null);
    }

    @Test
    public void defaultResult_NormalLengthFullText() {
        String fullText = "Hello world!";
        String defaultResult = titleExtractor.defaultResult(fullText);
        Assert.assertThat(defaultResult.length(), Matchers.lessThanOrEqualTo(TitleExtractor.DEFAULT_LENGTH_OF_TITLE_FROM_START));
        Assert.assertEquals(fullText, defaultResult);
    }

    @Test
    public void defaultResult_TooLongFullText() {
        String tooLongFullText = "России отдали победу в эстафете юниорского ЧМ, но изучили фотофиниш и отобрали золото";
        String defaultResult = titleExtractor.defaultResult(tooLongFullText);
        Assert.assertThat(defaultResult.length(), Matchers.lessThanOrEqualTo(TitleExtractor.DEFAULT_LENGTH_OF_TITLE_FROM_START));
        Assert.assertThat(tooLongFullText.length(), Matchers.greaterThan(TitleExtractor.DEFAULT_LENGTH_OF_TITLE_FROM_START));
        Assert.assertEquals(tooLongFullText.substring(0, TitleExtractor.DEFAULT_LENGTH_OF_TITLE_FROM_START), defaultResult);
    }

    @Test
    public void getTitleFromFullText_DocumentIsNull() {
        String fullText = "России отдали победу в эстафете юниорского ЧМ, но изучили фотофиниш и отобрали золото";
        long databaseId = 123L;
        String query = "футбол";
        Mockito.doReturn(directory).when(inMemoryIndexComponent).index(Mockito.any());
        Mockito.doReturn(null).when(inMemoryIndexComponent)
                .searchIndex(Mockito.any(Query.class), Mockito.any());
        String title = titleExtractor.getTitleFromFullText(fullText, databaseId, query);
        Assert.assertNotNull(title);
        Assert.assertEquals(titleExtractor.defaultResult(fullText), title);
    }

    @Test
    public void getTitleFromFullText_ParseException() {
        String fullText = "России отдали победу в эстафете юниорского ЧМ, но изучили фотофиниш и отобрали золото";
        long databaseId = 123L;
        String query = "футбол";
        try (MockedStatic<SearchIndexDocumentConverter> converter = Mockito.mockStatic(SearchIndexDocumentConverter.class)) {
            converter.when(() -> SearchIndexDocumentConverter.createQueryFullText(query)).thenThrow(ParseException.class);
            Mockito.doReturn(directory).when(inMemoryIndexComponent).index(Mockito.any());
            String title = titleExtractor.getTitleFromFullText(fullText, databaseId, query);
            Assert.assertNotNull(title);
            Assert.assertEquals(titleExtractor.defaultResult(fullText), title);
        }
    }

    @Test
    public void getTitleFromFullText_IndexedTitle() {
        String fullText = "России отдали победу в эстафете юниорского ЧМ, но изучили фотофиниш и отобрали золото";
        long databaseId = 123L;
        String query = "победу";
        String chunk = titleExtractor.getChunks(fullText).get(0);
        String chunkWithDatabaseId = databaseId + ":" + chunk;
        Mockito.doReturn(directory).when(inMemoryIndexComponent).index(Mockito.any());
        Mockito.doReturn(document).when(inMemoryIndexComponent)
                .searchIndex(Mockito.any(Query.class), Mockito.any());
        Mockito.doReturn(databaseField).when(document).getField(SearchIndexDocumentConverter.DATABASE_ID_FIELD);
        Mockito.doReturn(String.valueOf(databaseId)).when(databaseField).stringValue();
        Mockito.doReturn(fullTextField).when(document).getField(SearchIndexDocumentConverter.FULL_TEXT_FIELD);
        Mockito.doReturn(chunkWithDatabaseId).when(fullTextField).stringValue();
        Mockito.doNothing().when(inMemoryIndexComponent).deleteByDatabaseId(databaseId, directory);
        String title = titleExtractor.getTitleFromFullText(fullText, databaseId, query);
        Assert.assertTrue(title.contains(query));
        Assert.assertEquals(chunk, title);
    }

}
