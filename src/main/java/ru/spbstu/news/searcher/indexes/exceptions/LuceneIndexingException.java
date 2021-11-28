package ru.spbstu.news.searcher.indexes.exceptions;

public class LuceneIndexingException extends Exception {

    public LuceneIndexingException(String message) {
        super(message);
    }

    public LuceneIndexingException(String message, Throwable cause) {
        super(message, cause);
    }
}
