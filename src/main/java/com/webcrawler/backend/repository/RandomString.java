package com.webcrawler.backend.repository;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A companion class for the generation of random alphanumeric strings. It keeps
 * track of already generated IDs in order to avoid duplication.
 */
final class RandomString {
	/**
	 * The numeric value of the ASCII character 'a'
	 */
	private static final int leftLimit = 97;
	/**
	 * The numeric value of the ASCII character 'z'
	 */
	private static final int rightLimit = 122;
	private static final Random random = new Random();

	private static final Set<String> GENERATED_IDS = new HashSet<>();

	/**
	 * Generate an alphanumeric string to be used as an ID
	 * 
	 * @param length The length of the desired string
	 * @return An alphanumeric string
	 */
	static String getString(int length) {
		String id;
		do {
			id = generateID(length);
		} while (GENERATED_IDS.contains(id));
		GENERATED_IDS.add(id);
		return id;
	}
	
	private static String generateID(int length) {
		return random.ints(leftLimit, rightLimit + 1)
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}
}