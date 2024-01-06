package com.webcrawler.backend.search;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpDownloader {

	private static final Logger logger = LoggerFactory.getLogger(HttpDownloader.class);

	private static final HttpClient CLIENT = HttpClient.newBuilder().version(Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	public CompletableFuture<String> downloadPage(String url) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(5L)).build();

		return CLIENT.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.exceptionally(throwable -> logError(throwable, url));
	}

	private String logError(Throwable t, String page) {
		logger.error("Error while downloading page {}\n Error: {}", page, t.getMessage());
		return "";
	}

}
