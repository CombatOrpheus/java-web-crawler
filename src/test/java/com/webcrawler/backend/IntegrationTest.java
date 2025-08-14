package com.webcrawler.backend;

import com.google.gson.Gson;
import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.json.PostResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String BASE_URL = "https://jsoup.org/";
    private static final String KEYWORD = "jsoup";

    @BeforeAll
    public static void setUp() {
        Main.main(new String[]{BASE_URL});
        Spark.awaitInitialization();
    }

    @AfterAll
    public static void tearDown() {
        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    public void testCrawl() throws Exception {
        // Start crawl
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/crawl"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"keyword\":\"" + KEYWORD + "\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        PostResponse postResponse = new Gson().fromJson(response.body(), PostResponse.class);
        String crawlId = postResponse.getId();
        assertNotNull(crawlId);

        // Poll for result
        GetResponse getResponse = null;
        for (int i = 0; i < 60; i++) {
            Thread.sleep(1000);
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:4567/crawl/" + crawlId))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            getResponse = new Gson().fromJson(response.body(), GetResponse.class);
            if ("done".equals(getResponse.getStatus())) {
                break;
            }
        }

        assertNotNull(getResponse);
        assertEquals("done", getResponse.getStatus().toString().toLowerCase());
        assertNotNull(getResponse.getResults());
        assertFalse(getResponse.getResults().isEmpty());
    }
}
