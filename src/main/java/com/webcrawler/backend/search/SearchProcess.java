package com.webcrawler.backend.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.json.Status;

class SearchProcess {
	
	private final String keyword;
	private final List<String> results = new ArrayList<>();
	private final Set<String> searchedPages = new HashSet<>();
	private boolean complete;
	private final Predicate<String> SEARCH_PATTERN;

	SearchProcess(String keyword) {
		this.keyword = keyword;
		this.complete = false;
		this.SEARCH_PATTERN = Pattern.compile(keyword).asMatchPredicate();
	}
	
	public void start() {
		do {
			List<Page> pageList = DownloadProcess.getDownloadedPages();
			for (Page page: pageList) {
				String url = page.url();
				String contents = page.contents();
				
				searchedPages.add(url);
				boolean containsKeyword = SEARCH_PATTERN.test(contents);
				if (containsKeyword) {
					results.add(url);
				}
			}
			
			if (DownloadProcess.getVisitedPages().containsAll(searchedPages)) {
				this.complete = true;
			}
		} while (complete);
	}

	public GetResponse getResult() {
		return new GetResponse(keyword, complete ? Status.ACTIVE : Status.DONE, results);
	}

}
