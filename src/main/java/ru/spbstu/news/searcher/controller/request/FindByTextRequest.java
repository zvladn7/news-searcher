package ru.spbstu.news.searcher.controller.request;

public class FindByTextRequest {

    private final String query;

    public FindByTextRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

}
