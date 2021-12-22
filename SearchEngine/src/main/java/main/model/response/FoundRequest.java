package main.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoundRequest {
    private String site;
    private String siteName;
    private String url;
    private String title;
    private String snippet;
    private double relevance;
}
