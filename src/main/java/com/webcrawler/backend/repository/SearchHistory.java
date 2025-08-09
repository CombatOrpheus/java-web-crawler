package com.webcrawler.backend.repository;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.search.SearchScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A simple repository to keep track of running queries and their status.
 */
public final class SearchHistory {
	
	private static final Map<String, String> PREVIOUS_SEARCHES = new HashMap<>();

	/**
	 * Adds a new keyword to the search history. If the keyword has been searched before,
	 * it returns the existing ID. Otherwise, it generates a new ID, stores it, and returns it.
	 * @param keyword the keyword to add
	 * @return the ID for the keyword
	 */
	public static String addByKeyword(String keyword) {
		if (PREVIOUS_SEARCHES.containsKey(keyword)) {
			return PREVIOUS_SEARCHES.get(keyword);
		}

        String id = RandomString.getString(32);
		PREVIOUS_SEARCHES.put(keyword, id);
		return id;
	}

	/**
	 * Gets the results of a search by its ID.
	 * @param id the ID of the search
	 * @param scheduler the SearchScheduler to use to get the results
	 * @return an Optional containing the search results, or an empty Optional if the ID is not found
	 */
	public static Optional<GetResponse> getById(String id, SearchScheduler scheduler) {
		return scheduler.getResults(id);
	}
}
