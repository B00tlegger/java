package main.controllers;

import main.properties.AppProp;
import main.model.response.Response;
import main.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class SiteUploaderController {

    private SiteUploadService siteUploadService;
    private AppProp appProp;

    @Autowired
    public SiteUploaderController (SiteUploadService siteUploadService, AppProp appProp) {
        this.siteUploadService = siteUploadService;
        this.appProp = appProp;
    }

    @GetMapping("/startIndexing")
    public ResponseEntity startIndexing () {
        Response response = siteUploadService.startIndexing();
        return response.isResult() ? new ResponseEntity<>(response, HttpStatus.CREATED) :
                new ResponseEntity<>(response, HttpStatus.PROCESSING);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stopIndexing () {
        Response response = siteUploadService.stopIndexing();
        return response.isResult() ? ResponseEntity.ok(response) :
                new ResponseEntity<>(response, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @PostMapping("/indexPage")
    public ResponseEntity indexPage (@RequestParam String url) {
        Response response = siteUploadService.indexPage(url);
        return response.isResult() ? ResponseEntity.ok(response) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/statistics")
    public ResponseEntity statistics () {
        return ResponseEntity.ok(siteUploadService.statistics());
    }

}
