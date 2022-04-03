package ru.spbstu.news.searcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.spbstu.news.searcher.mainpage.LanguageChangeTest;
import ru.spbstu.news.searcher.mainpage.MainPageTest;
import ru.spbstu.news.searcher.mainpage.QuerySpecificPagesTest;
import ru.spbstu.news.searcher.startpage.SearchInputHistoryTest;
import ru.spbstu.news.searcher.startpage.SearchStartPageTest;

@Suite.SuiteClasses({
        MainPageTest.class,
        SearchStartPageTest.class,
        LanguageChangeTest.class,
        SearchInputHistoryTest.class,
        QuerySpecificPagesTest.class
})
@RunWith(Suite.class)
public class TestSuite {
}
