package com.webcrawler.backend.json;

import com.google.gson.Gson;

public class JsonHandler {
	
	private static final Gson converter = new Gson();

	public static String getKeyword(String body) {
		return converter.fromJson("id", String.class);
	}
	
	public static <T> String toJson(T input) {
		return converter.toJson(input);
	}

}
