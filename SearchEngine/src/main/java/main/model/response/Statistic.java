package main.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import main.model.entityes.Site;

import java.util.List;
@Data
@AllArgsConstructor
public class Statistic {
    private TotalStatistic total;
    private List<DetailedStatistic> detailed;
    @Data
    public static class DetailedStatistic {
        private String url;
        private String name;
        private Site.StatusType status;
        private long statusTime;
        @JsonInclude
        private String error;
        private long pages;
        private long lemmas;

        public DetailedStatistic(String url, String name, Site.StatusType status, long statusTime, String error, long pages, long lemmas) {
            this.url = url;
            this.name = name;
            this.status = status;
            this.statusTime = statusTime;
            this.error = error;
            this.pages = pages;
            this.lemmas = lemmas;
        }
    }

    @Data
    public static class TotalStatistic {
        private long sites;
        private long pages;
        private long lemmas;
        private boolean isIndexing;

        public TotalStatistic(long sites, long pages, long lemmas){
            this.sites = sites;
            this.pages = pages;
            this.lemmas = lemmas;
            this.isIndexing = true;
        }
    }
}

