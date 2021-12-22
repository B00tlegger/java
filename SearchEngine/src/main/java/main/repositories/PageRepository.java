package main.repositories;

import main.model.entityes.Page;
import main.repositories.custom.RepositoryCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer>, RepositoryCustom {

    List<Page> findPagesByIdIn (List<Integer> pagesId);

    Page findPageByPathAndSiteId(String path, int siteId);

    List<Page> findPagesBySiteId(int siteId);

    @Transactional
    void removeBySiteId(int siteId);

    long countBySiteId(int siteId);

    @Query("Select p.id from Page p where id in :id and content like :text")
    List<Integer> findPagesByIdInAndContentLike (@Param("id") Set<Integer> findPages, @Param("text") String searchText);
}
