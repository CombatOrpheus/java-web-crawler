package com.webcrawler.backend.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.webcrawler.backend.json.PostResponse;

class SearchSchedulerTest {
	
	private SearchScheduler scheduler;
	
	@BeforeEach
	void setUp() {
		this.scheduler = new SearchScheduler();
	}
	
	@AfterEach
	void tearDown() {
		this.scheduler = null;
	}
	
	@Test
	void invalidKeywordShouldReturnEmptyOptional() {
		Optional<PostResponse> result = scheduler.validateAndStartSearch("");
		assertTrue(result.isEmpty());
	}

	@Test
	void aValidStringShouldReturnAnId() {
		String string = "testString";
		Optional<PostResponse> result = scheduler.validateAndStartSearch(string);
		assertNotNull(getString(result));
	}
	
	@Test
	void sameStringShouldReturnSameId() {
		String string = "testString";
		
		Optional<PostResponse> result = scheduler.validateAndStartSearch(string);
		String firstId = getString(result);
		
		result = scheduler.validateAndStartSearch(string);
		String secondId = getString(result);
		
		assertEquals(firstId, secondId);
	}
	
	@Test
	void keywordCasingIsIrrelevant() {
		Stream<String> strings = Stream.of("keyword", "Keyword", "KEYWORD", "KeYwOrD");
		List<String> results = strings.map(scheduler::validateAndStartSearch).map(this::getString).toList();
		String id = results.get(0);
		for (String result: results) {
			assertEquals(id, result);
		}
	}
	
	@Test
	void invalidIdReturnsEmptyResult() {
		var result = scheduler.getResults("id");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void idShouldReturnValidResult() {
		Optional<PostResponse> id = scheduler.validateAndStartSearch("keyword");
		var result = scheduler.getResults(getString(id));
		
		assertTrue(result.isPresent());
	}
	
	private String getString(Optional<PostResponse> optional) {
		return optional.get().id();
	}
}
