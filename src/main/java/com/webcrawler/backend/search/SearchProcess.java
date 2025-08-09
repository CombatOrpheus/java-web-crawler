package com.webcrawler.backend.search;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.json.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements the search process for a particular keyword and is
 * created by {@link SearchScheduler}. The only method that has a return is the
 * {@link SearchProcess#getResult() getResult()} method..
 */
final class SearchProcess {
	
	private final Logger logger = LoggerFactory.getLogger(SearchProcess.class);

	private final String keyword;
	private final DownloadProcessInterface downloadProcess;
	private final List<String> results = new ArrayList<>();
	private final Set<String> searchedPages = new HashSet<>();
	private boolean complete;

	SearchProcess(String keyword, DownloadProcessInterface downloadProcess) {
		this.keyword = keyword;
		this.downloadProcess = downloadProcess;
		this.complete = false;
	}

	/**
	 * The main search process, which runs in a loop for as long as there are new
	 * pages to search. It only completes when the {@link DownloadProcess} is done.
	 */
	public void start() {
		logger.info("Starting search process for keyword \"{}\"", keyword);
		while (!complete) {
			List<Page> pageList = downloadProcess.getDownloadedPages();
			for (Page page : pageList) {
				if (!searchedPages.contains(page.getUrl())) {
					searchedPages.add(page.getUrl());
					if (page.getContents().contains(keyword)) {
						logger.info("Found keyword {} in page {}", keyword, page.getUrl());
						results.add(page.getUrl());
					}
				}
			}

			if (downloadProcess.isComplete()) {
				this.complete = true;
			}
		}
		logger.info("Keyword search for \"{}\" complete!", keyword);
	}

	public GetResponse getResult() {
		return new GetResponse(keyword, complete ? Status.DONE : Status.ACTIVE, results);
	}

}
