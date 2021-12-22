package main.controllers;

import main.model.response.Response;
import main.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TextFinderController {

    @Autowired
    private TextFinderService textFinderService;

    @GetMapping("/search")
    public ResponseEntity search (@RequestParam String query,
                                  @RequestParam(required = false) String site,
                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                  @RequestParam(required = false, defaultValue = "20") int limit) {
        return query.isBlank() ?
                new ResponseEntity<>(new Response(false, "Задан пустой поисковый запрос"), HttpStatus.BAD_REQUEST)
                : ResponseEntity.ok(textFinderService.search(query, site, offset, limit));
    }
}
