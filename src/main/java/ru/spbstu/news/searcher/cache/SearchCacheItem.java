package ru.spbstu.news.searcher.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchCacheItem {

    private String title;
    private String url;
    private List<String> imageUrls;

}
