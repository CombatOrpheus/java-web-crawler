package com.webcrawler.backend.search;

import static com.webcrawler.backend.constants.Constants.LENGTH_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.repository.QueryRepository;

/**
 * The main class for the starting and querying the search processes.
 * Internally, it keeps track of the keywords being searched in order to avoid
 * multiple searches for the same keyword.
 */
public final class SearchScheduler {
	/**
	 * A simple thread pool for keeping multiple jobs running concurrently.
	 */
	private static final ForkJoinPool threadPool = ForkJoinPool.commonPool();

	/**
	 * A map containing the ID and the corresponding search process.
	 */
	private static final Map<String, SearchProcess> SEARCH_TASKS = new HashMap<>();

	/**
	 * Validates the keyword and starts the search process for it.
	 * 
	 * @param keyword The keyword to be validated
	 * @return An {@link Optional} containing the ID of the search process if the
	 *         keyword is valid, otherwise, an empty Optional. It returns the same
	 *         ID if the same keyword is used multiple times.
	 */
	public static Optional<String> validateAndStartSearch(String keyword) {
		if (isValid(keyword)) {
			String id = RandomString.getString(LENGTH_ID);
			QueryRepository.addById(id, keyword);
			SearchProcess search = new SearchProcess(keyword);
			SEARCH_TASKS.put(id, search);
			threadPool.execute(() -> search.start());
			return Optional.of(id);
		}
		return Optional.empty();
	}

	/**
	 * Get the results of the search process for the specified ID.
	 * 
	 * @param id The ID, previously generated by the {@link RandomString#getString}
	 *           method.
	 * @return If the ID exists, an {@link Optional} containing the
	 *         {@link GetResponse} results, otherwise, and empty Optional.
	 */
	public static Optional<GetResponse> getResults(String id) {
		if (SEARCH_TASKS.containsKey(id)) {
			SearchProcess searchProcess = SEARCH_TASKS.get(id);
			return Optional.of(searchProcess.getResult());
		}
		return Optional.empty();
	}

	private static boolean isValid(String keyword) {
		return keyword.length() >= 4 && keyword.length() <= 32;
	}
}
