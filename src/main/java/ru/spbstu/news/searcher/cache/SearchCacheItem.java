package ru.spbstu.news.searcher.cache;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SearchCacheItem {

    private String title;
    private String url;
    private List<String> imageUrls;

    public SearchCacheItem() {
    }

    public SearchCacheItem(@NotNull String title,
                           @NotNull String url,
                           @Nullable List<String> imageUrls) {
        Validate.notNull(title);
        Validate.notNull(url);
        this.title = title;
        this.url = url;
        this.imageUrls = imageUrls;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        Validate.notNull(title);
        this.title = title;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NotNull String url) {
        Validate.notNull(url);
        this.url = url;
    }

    @Nullable
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(@Nullable List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
