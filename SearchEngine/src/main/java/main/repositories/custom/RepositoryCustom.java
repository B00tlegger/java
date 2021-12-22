package main.repositories.custom;

import main.model.entityes.Index;
import main.model.entityes.Lemma;
import java.util.List;
import java.util.Set;

public interface RepositoryCustom {

    void saveIndexes(List<Index> indexList);
    void saveLemmas(Set<Lemma> lemmaSet);
    void setLemmas(Set<Lemma> lemmas, String changes);
}
