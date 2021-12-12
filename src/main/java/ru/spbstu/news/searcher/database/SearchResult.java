package ru.spbstu.news.searcher.database;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "searching")
public class SearchResult {

    public SearchResult(final String url, final List<String> imageUrls) {
        this.url = url;
        this.imageUrls = imageUrls;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition="TEXT")
    private String url;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imageUrls;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(url, that.url) &&
                Iterables.elementsEqual(
                        imageUrls != null ? imageUrls : new ArrayList<>(),
                        that.imageUrls != null ? that.imageUrls : new ArrayList<>());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, imageUrls);
    }
}

