package com.webcrawler.backend.json;

/**
 * A class that represents the response for a POST /crawl request.
 */
public class PostResponse {
    private final String id;

    public PostResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
