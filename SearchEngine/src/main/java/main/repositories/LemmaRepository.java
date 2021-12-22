package main.repositories;

import main.model.entityes.Lemma;
import main.repositories.custom.RepositoryCustom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer>, RepositoryCustom {
    List<Lemma> findLemmasBySiteId (int id);

    @Transactional
    void removeBySiteId(int siteId);

    long countBySiteId(int siteId);

    List<Lemma> findLemmasByLemmaInAndSiteId(List<String> lemmaSet, int siteId);

    List<Lemma> findLemmasByLemmaInAndSiteIdIn(List<String> lemmaSet, List<Integer> collect);

    @Query("SELECT l FROM Lemma l JOIN Index i ON l.id = i.lemmaId WHERE i.pageId = ?1")
    List<Lemma>findLemmasByPageId(int pageId);

    Collection<? extends Lemma> findLemmasByLemmaIn (List<String> lemmaList);
}
