package ru.spbstu.news.searcher.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchResultRepository extends JpaRepository<SearchResult, Long> {
    Optional<SearchResult> findById(Long id);
}
