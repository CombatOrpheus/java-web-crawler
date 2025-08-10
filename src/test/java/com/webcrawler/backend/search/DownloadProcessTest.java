package com.webcrawler.backend.search;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class DownloadProcessTest {

    @Test
    public void testLinkExtraction() {
        // Given
        String baseUrl = "http://example.com";
        DownloadProcess downloadProcess = new DownloadProcess(baseUrl);

        String htmlContent = "<html><body>" +
                "<a href=\"/page1.html\">Page 1</a>" +
                "<a href=\"http://example.com/page2.html\">Page 2</a>" +
                "<a href=\"http://another.com/page3.html\">Page 3</a>" +
                "<a href=\"/page1.html\">Duplicate Page 1</a>" +
                "</body></html>";

        Page page = new Page(baseUrl, CompletableFuture.completedFuture(htmlContent));

        // When
        List<String> links = downloadProcess.getLinks(page);

        // Then
        assertThat(links, hasSize(2));
        assertThat(links, containsInAnyOrder(
                "http://example.com/page1.html",
                "http://example.com/page2.html"
        ));
    }
}
