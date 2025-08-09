package com.webcrawler.backend.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * A simple asynchronous HTTP downloader.
 */
public final class HttpDownloader {

	private static final Logger logger = LoggerFactory.getLogger(HttpDownloader.class);

	private static final HttpClient CLIENT = HttpClient.newBuilder().version(Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	/**
	 * Downloads the content of a URL.
	 * @param url the URL to download
	 * @return a {@link CompletableFuture} with the content of the URL, or an empty string if an error occurs
	 */
	public CompletableFuture<String> downloadPage(String url) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(5L)).build();

		return CLIENT.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(String::toLowerCase)
				.exceptionally(throwable -> logError(throwable, url));
	}

	private String logError(Throwable t, String page) {
		logger.error("Error while downloading page {}\n Error: {}", page, t.getMessage());
		return "";
	}

}
