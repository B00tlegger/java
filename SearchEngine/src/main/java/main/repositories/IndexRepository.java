package main.repositories;

import main.model.entityes.Index;
import main.model.entityes.Lemma;
import main.model.entityes.Page;
import main.repositories.custom.RepositoryCustom;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface IndexRepository extends CrudRepository<Index, Integer>, RepositoryCustom {
    @Modifying
    @Transactional
    void deleteByPageId(int pageId);

    @Transactional
    void deleteAllIndexesByPageIdIn (List<Integer> collect);

    List<Index> findIndexesByLemmaIdIn (List<Integer> collect);
}
