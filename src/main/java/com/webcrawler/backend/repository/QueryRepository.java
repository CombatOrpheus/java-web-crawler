package com.webcrawler.backend.repository;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.search.SearchScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A simple repository to keep track of running queries and their status. All of
 * its methods are static in order to avoid multiple objects being created.
 */
public final class QueryRepository {
	private SearchScheduler scheduler;
	
	public QueryRepository(SearchScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	private static final Map<String, String> PREVIOUS_SEARCHES = new HashMap<>();

	public static String addByKeyword(String keyword) {
		if (PREVIOUS_SEARCHES.containsKey(keyword)) {
			return PREVIOUS_SEARCHES.get(keyword);
		}

        String id = RandomString.getString(32);
		PREVIOUS_SEARCHES.put(keyword, id);
		return id;
	}
	
	public Optional<GetResponse> getById(String id) {
		return scheduler.getResults(id);
	}
}
