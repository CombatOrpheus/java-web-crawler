package com.webcrawler.backend.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageTest {

    @ParameterizedTest
    @CsvSource({
            "https://example.com, /path, https://example.com/path",
            "https://example.com/path/, ./subpath, https://example.com/path/subpath",
            "https://example.com/path/to/page, ../, https://example.com/path/",
            "https://example.com, https://google.com, https://google.com"
    })
    void testMapToAbsoluteLink(String baseUrl, String link, String expected) {
        Page page = new Page(baseUrl, CompletableFuture.completedFuture(""));
        assertEquals(expected, page.mapToAbsoluteLink(link));
    }

    @Test
    void testIsValidLink() {
        Page page = new Page("https://example.com", CompletableFuture.completedFuture(""));
        assertTrue(page.isValidLink("https://example.com"));
        assertTrue(page.isValidLink("http://example.com"));
        assertTrue(page.isValidLink("https://example.com/path"));
        assertTrue(page.isValidLink("https://example.com/path?query=string"));
        assertTrue(page.isValidLink("https://example.com/path#fragment"));
        assertFalse(page.isValidLink("tel:1234567890"));
        assertFalse(page.isValidLink("mailto:test@example.com"));
        assertFalse(page.isValidLink("javascript:alert('xss')"));
        assertFalse(page.isValidLink("ftp://example.com"));
        assertFalse(page.isValidLink(""));
        assertFalse(page.isValidLink(" "));
    }
}
