package main.repositories.custom;

import main.model.entityes.Index;
import main.model.entityes.Lemma;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

public class RepositoryCustomImpl implements RepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveIndexes (List<Index> indexList) {
        if(indexList.size() > 0) {
            int queryCount = 0;
            String query = "INSERT INTO `index`(lemma_id, page_id, `rank`) VALUES";
            StringBuilder stringBuilder = new StringBuilder();
            for(Index index : indexList) {
                if(queryCount > 4000000 || stringBuilder.capacity() >= 4000000) {
                    entityManager.createNativeQuery(query.concat(stringBuilder.toString())).executeUpdate();
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append((stringBuilder.length() != 0) ? ", " : "").append("(")
                             .append(index.getLemmaId()).append(", ")
                             .append(index.getPageId()).append(", ")
                             .append(index.getRank()).append(")");
                queryCount++;
            }
            entityManager.createNativeQuery(query.concat(stringBuilder.toString())).executeUpdate();
        }
    }

    @Override
    @Transactional
    public void saveLemmas (Set<Lemma> lemmaSet) {
        if(lemmaSet.size() > 0) {
            int queryCount = 0;
            String query = "INSERT INTO lemma(lemma, frequency, site_id) VALUES";
            StringBuilder stringBuilder = new StringBuilder();
            for(Lemma lemma : lemmaSet) {
                if(queryCount > 4000000 || stringBuilder.capacity() >= 4000000) {
                    entityManager.createNativeQuery(query.concat(stringBuilder.toString())).executeUpdate();
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append((stringBuilder.length() != 0) ? ", " : "").append("(")
                             .append("'").append(lemma.getLemma()).append("'").append(", ")
                             .append(lemma.getFrequency()).append(", ")
                             .append(lemma.getSiteId()).append(")");
                queryCount++;
            }
            entityManager.createNativeQuery(query.concat(stringBuilder.toString())).executeUpdate();

        }
    }

    @Override
    @Transactional
    public void setLemmas (Set<Lemma> lemmas, String changes) {
        String sql = "UPDATE lemma SET frequency = frequency " + changes + " WHERE id in( ";
        StringBuilder builder = new StringBuilder();
        for(Lemma lemma : lemmas) {
            builder.append((builder.length() == 0) ? "" + lemma.getId() : ", " + lemma.getId());
        }
        entityManager.createNativeQuery(sql + builder + " ) ").executeUpdate();
    }
}
