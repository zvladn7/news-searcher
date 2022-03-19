package ru.spbstu.news.searcher.controller.result;

import lombok.*;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ImageItem {

    private long id;
    private String imageUrl;
    private String title;
    private String link;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageItem imageItem = (ImageItem) o;
        return id == imageItem.id && Objects.equals(imageUrl, imageItem.imageUrl) && Objects.equals(link, imageItem.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, link);
    }
}
