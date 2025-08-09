package com.webcrawler.backend;

import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.json.JsonHandler;
import com.webcrawler.backend.json.PostResponse;
import com.webcrawler.backend.repository.SearchHistory;
import com.webcrawler.backend.search.DownloadProcess;
import com.webcrawler.backend.search.SearchScheduler;
import spark.Request;
import spark.Response;

import java.util.Optional;
import java.util.concurrent.Executors;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * The main entry point for the web crawler application.
 */
public class Main {
	/**
	 * The main method that starts the web crawler application.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

        DownloadProcess downloadProcess = new DownloadProcess(args[0]);
        Executors.newSingleThreadExecutor().submit(downloadProcess);
        SearchScheduler searchScheduler = new SearchScheduler(downloadProcess);
		
		post("/crawl", (Request req, Response res) -> {
			String keyword = JsonHandler.getKeyword(req.body());
			Optional<PostResponse> id = searchScheduler.validateAndStartSearch(keyword);

			res.type("application/json");
			res.status(id.isPresent() ? 200 : 400);

			return id.map(JsonHandler::toJson).orElse("Invalid ID");
		});

		get("/crawl/:id", (req, res) -> {
			String id = req.params("id");
			Optional<GetResponse> result = SearchHistory.getById(id, searchScheduler);

			res.type("application/json");
			res.status(result.isPresent() ? 200 : 400);
			return result.map(JsonHandler::toJson).orElse("Unknown ID");
		});
	}
}