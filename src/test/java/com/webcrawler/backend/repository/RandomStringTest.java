package com.webcrawler.backend.repository;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomStringTest {

	private static final int LENGTH = 10;
	private static final Pattern EXPECTED_PATTERN = Pattern.compile("([a-z]|[0-9]){" + LENGTH + "}");

	@Test
	void testGetString() {
		String randomString = RandomString.getString(LENGTH);
		assertEquals(LENGTH, randomString.length());
		assertThat(randomString, matchesPattern(EXPECTED_PATTERN));
	}

	@Test
	void shouldAllBeDifferent() {
		int size = 100;
		Set<String> generated = IntStream.range(0, size)
				.mapToObj(RandomString::getString)
				.collect(Collectors.toSet());

		assertEquals(size, generated.size());
	}

}
