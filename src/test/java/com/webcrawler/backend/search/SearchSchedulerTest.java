package com.webcrawler.backend.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.webcrawler.backend.json.PostResponse;

class SearchSchedulerTest {

	@Test
	void aValidStringShouldReturnAnId() {
		String string = "testString";
		Optional<PostResponse> result = SearchScheduler.validateAndStartSearch(string);
		assertNotNull(result.get().id());
	}
}
