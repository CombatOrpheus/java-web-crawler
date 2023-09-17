package com.webcrawler.backend.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.json.Status;

/**
 * This class implements the search process for a particular keyword and is
 * created by {@link SearchScheduler}. The only method that has a return is the
 * {@link SearchProcess#getResult() getResult()} method..
 */
final class SearchProcess {
	
	private final Logger logger = LoggerFactory.getLogger(SearchProcess.class);

	private final String keyword;
	private final List<String> results = new ArrayList<>();
	private final Set<String> searchedPages = new HashSet<>();
	private boolean complete;

	SearchProcess(String keyword) {
		this.keyword = keyword;
		this.complete = false;
	}

	/**
	 * The main search process, which runs in a loop for as long as there are new
	 * pages to search. It only completes when the {@link DownloadProcess} is done.
	 */
	public void start() {
		logger.info("Starting search process for keyword \"{}\"", keyword);
		do {
			List<Page> pageList = DownloadProcess.getDownloadedPages();
			for (Page page : pageList) {
				searchedPages.add(page.url());
				if (page.contents().contains(keyword)) {
					logger.info("Found keyword {} in page {}", keyword, page.url());
					results.add(page.url());
				}
			}

			if (DownloadProcess.getVisitedPages().containsAll(searchedPages)) {
				this.complete = true;
			}
		} while (complete);
		logger.info("Keyword search for \"{}\" complete!", keyword);
	}

	public GetResponse getResult() {
		return new GetResponse(keyword, complete ? Status.ACTIVE : Status.DONE, results);
	}

}
