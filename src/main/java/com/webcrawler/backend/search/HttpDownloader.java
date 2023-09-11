package com.webcrawler.backend.search;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class HttpDownloader {
	private static final HttpClient CLIENT = HttpClient.newBuilder()
			.version(Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10))
			.build();
	
	public static CompletableFuture<String> downloadPage(String url) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.timeout(Duration.ofSeconds(5L))
				.build();
		
		return CLIENT.sendAsync(request, BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.exceptionally(t -> "");
	}

}
