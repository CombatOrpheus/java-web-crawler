package com.webcrawler.backend;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.util.Optional;

import com.webcrawler.backend.crawler.Crawler;
import com.webcrawler.backend.json.GetResponse;
import com.webcrawler.backend.json.JsonHandler;
import com.webcrawler.backend.repository.QueryRepository;

import spark.Request;
import spark.Response;

public class Main {
	public static void main(String[] args) {
		post("/crawl", (Request req, Response res) -> {
			String keyword = JsonHandler.getKeyword(req.body());
			Optional<String> id = Crawler.validateKeyword(keyword);

			res.type("application/json");
			res.status(id.isPresent() ? 200 : 400);

			return id.map(JsonHandler::toJson).orElseGet(() -> "Invalid ID");
		});

		get("/crawl/:id", (req, res) -> {
			String id = req.params("id");
			Optional<GetResponse> result = QueryRepository.getById(id);

			res.type("application/json");
			res.status(result.isPresent() ? 200 : 400);
			return result.map(JsonHandler::toJson).orElseGet(() -> "Unknow ID");
		});
	}
}