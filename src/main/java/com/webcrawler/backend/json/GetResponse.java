package com.webcrawler.backend.json;

import java.util.List;

import java.util.List;

/**
 * A class that represents the response for a GET /crawl/:id request.
 */
public class GetResponse {
    private final String keyword;
    private final Status status;
    private final List<String> results;

    public GetResponse(String keyword, Status status, List<String> results) {
        this.keyword = keyword;
        this.status = status;
        this.results = results;
    }

    public String getKeyword() {
        return keyword;
    }

    public Status getStatus() {
        return status;
    }

    public List<String> getResults() {
        return results;
    }
}
