package com.webcrawler.backend.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PageTest {

	private static final String base = "https://google.com";
	private Page page;

	@BeforeEach
	void setUp() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "contents");
		this.page = new Page(base, future);
	}

	@AfterEach
	void tearDown() {
		this.page = null;
	}

	@ParameterizedTest
	@ValueSource(strings = { "https://google.com/options", "https://google.com/search", "https://google.com/account" })
	void absoluteLinks(String link) {
		String actual = page.mapToAbsoluteLink(link);
		assertEquals(link, actual);
	}

	@ParameterizedTest
	@ValueSource(strings = { "anotherPage", "another.html", "./relative/link", "/multi/level/page" })
	void mapLinksToOtherPages(String link) {
		String actual = page.mapToAbsoluteLink(link);
		assertTrue(actual.startsWith(base));
	}

	// TODO Add tests for handling of links above the current level.

	@ParameterizedTest
	@ValueSource(strings = { "#A header", "tel:+12342523", "mail:anEmail@somewhere.com" })
	void invalidLinks(String link) {
		assertFalse(page.isValidLink(link));
	}
}
