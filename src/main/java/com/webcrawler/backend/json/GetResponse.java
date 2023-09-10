package com.webcrawler.backend.json;

import java.util.List;

public record GetResponse(String keyword, Status status, List<String> results) {

}
