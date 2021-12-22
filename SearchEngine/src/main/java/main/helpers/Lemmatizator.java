package main.helpers;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Lemmatizator {
    @Getter
    private String text;

    @Getter
    private final HashMap<String, Integer> lemmaCount = new HashMap<>();
    private LuceneMorphology luceneMorphology;

    public Lemmatizator() {
    }

    public Lemmatizator(String text) {
        this.text = text.toLowerCase(Locale.ROOT);
    }

    @SneakyThrows
    public Set<String> processText() {
        luceneMorphology = new RussianLuceneMorphology();
        String[] words = text.replaceAll("[^а-яА-Я]", " ").split(" +");
        for (String word : words) {
            if (!word.isBlank()) {
                if (wordFilter(word.trim())) {
                    List<String> lemWord = luceneMorphology.getNormalForms(word);
                    int index = 0;
                    for (int i = 0; i < lemWord.size(); i++) {
                        index = (lemmaCount.containsKey(lemWord.get(i))) ? i : index;
                    }
                    int value = lemmaCount.getOrDefault(lemWord.get(index), 0);
                    lemmaCount.put(lemWord.get(index), value + 1);

                }
            }
        }
        return getAllLemmas();
    }

    private boolean wordFilter(String word) {
        String info = null;
        if (word.length() > 0) {
            List<String> morphology = luceneMorphology.getMorphInfo(word);
            info = morphology.get(0).substring(morphology.get(0).indexOf(" ")).trim();
        }
        assert info != null;
        return !info.equals("МЕЖД") && !info.equals("ПРЕДЛ") && !info.equals("СОЮЗ") && !info.equals("ЧАСТ");
    }

    public Set<String> getAllLemmas() {
        return lemmaCount.keySet();

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : lemmaCount.keySet()) {
            stringBuilder.append(string.concat(" - ").concat(String.valueOf(lemmaCount.get(string))).concat("\n"));
        }
        return stringBuilder.toString().trim();
    }

    public void setText(String text) {
        this.text = text.toLowerCase(Locale.ROOT);
    }
}
