package main.properties;

import lombok.Data;
import main.model.entityes.Site;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
@Data
@Component
@ConfigurationProperties(prefix = "application")
public class AppProp {
    private List<Site> sites;
}
