package com.webcrawler.backend.repository;

import static com.webcrawler.backend.constants.Constants.LENGTH_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.search.SearchScheduler;

/**
 * A simple repository to keep track of running queries and their status. All of
 * its methods are static in order to avoid multiple objects being created.
 */
public final class QueryRepository {
	
	private static final Map<String, String> PREVIOUS_SEARCHES = new HashMap<>();

	public static String addByKeyword(String keyword) {
		if (PREVIOUS_SEARCHES.containsKey(keyword)) {
			return PREVIOUS_SEARCHES.get(keyword);
		}
		
		String id = RandomString.getString(LENGTH_ID);
		PREVIOUS_SEARCHES.put(keyword, id);
		return id;
	}
	
	public static Optional<GetResponse> getById(String id) {
		return SearchScheduler.getResults(id);
	}
}
