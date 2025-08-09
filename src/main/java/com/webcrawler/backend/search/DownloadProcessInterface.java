package com.webcrawler.backend.search;

import java.util.List;
import java.util.Set;

public interface DownloadProcessInterface extends Runnable {
    boolean isComplete();
    List<Page> getDownloadedPages();
    Set<String> getVisitedPages();
}
