package com.webcrawler.backend.constants;

public final class Constants {
	public static final String BASE_URL = System.getenv("BASE_URL");
	/**
	 * A configurable parameter for setting the length of the generated ID. If the
	 * environment variable is not set, a default length of 10 is used.
	 */
	public static final int LENGTH_ID = Integer.parseInt(System.getenv("LENGTH_ID"));
}
