package com.webcrawler.backend.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class RandomStringTest {

	private static final int LENGTH = 10;
	private static final Pattern EXPECTED_PATTERN = Pattern.compile("([a-z]|[0-9]){" + LENGTH + "}");

	@Test
	void testGetString() {
		String randomString = RandomString.getString(LENGTH);
		assertEquals(LENGTH, randomString.length());
		assertThat(randomString, matchesPattern(EXPECTED_PATTERN));
	}

}
