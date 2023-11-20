package com.webcrawler.backend.json;

import com.google.gson.Gson;

public class JsonHandler {
	
	private static final Gson converter = new Gson();

	public static String getKeyword(String body) {
		PostRequest request = converter.fromJson(body, PostRequest.class);
		return request.keyword;
	}
	
	public static <T> String toJson(T input) {
		return converter.toJson(input);
	}
	
	private static class PostRequest {
		String keyword;
	}
}
