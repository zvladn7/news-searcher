package ru.spbstu.news.searcher.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchResultRepository extends JpaRepository<SearchResult, Long> {
}
