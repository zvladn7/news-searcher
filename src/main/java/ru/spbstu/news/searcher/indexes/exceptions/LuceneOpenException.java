package ru.spbstu.news.searcher.indexes.exceptions;

public class LuceneOpenException extends LuceneIndexingException {

    public LuceneOpenException(String message) {
        super(message);
    }

    public LuceneOpenException(String message, Throwable cause) {
        super(message, cause);
    }
}
