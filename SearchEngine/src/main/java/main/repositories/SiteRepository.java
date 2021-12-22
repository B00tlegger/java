package main.repositories;

import main.model.entityes.Site;
import main.repositories.custom.RepositoryCustom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer>, RepositoryCustom {

    Site findByUrl(String url);
}
