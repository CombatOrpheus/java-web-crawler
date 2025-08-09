package com.webcrawler.backend.json;

import com.google.gson.Gson;

/**
 * A handler for JSON serialization and deserialization.
 */
public class JsonHandler {
	
	private static final Gson converter = new Gson();

	/**
	 * Extracts the keyword from the request body.
	 * @param body the request body
	 * @return the keyword
	 */
	public static String getKeyword(String body) {
		PostRequest request = converter.fromJson(body, PostRequest.class);
		return request.keyword;
	}

	/**
	 * Converts an object to its JSON representation.
	 * @param input the object to convert
	 * @return the JSON representation of the object
	 * @param <T> the type of the object
	 */
	public static <T> String toJson(T input) {
		return converter.toJson(input);
	}
	
	private static class PostRequest {
		String keyword;
	}
}
