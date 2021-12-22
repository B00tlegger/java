package main.model.response;

import lombok.Data;

import java.util.List;
@Data
public class Response {
    private boolean result;
    private String error;
    private Statistic statistics;
    private Long count;
    private List<FoundRequest> data;

    public Response(boolean result){
        this.result = result;
    }

    public Response(boolean result, String error) {
        this.result = result;
        this.error = error;
    }

    public Response(boolean result, Statistic statistics) {
        this.result = result;
        this.statistics = statistics;
    }

    public Response (boolean result, long count, List<FoundRequest> data) {
        this.result = result;
        this.data = data;
        this.count = count;
    }
}
