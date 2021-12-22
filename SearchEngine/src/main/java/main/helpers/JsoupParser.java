package main.helpers;

import lombok.Data;
import main.model.entityes.Field;
import main.model.entityes.Page;
import main.model.entityes.Site;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
public class JsoupParser {
    private Document document;
    private final String url;
    private Site site;
    private HashMap<Field, HashMap<String, Integer>> elementsLemmas;
    private Connection.Response response;
    private static CopyOnWriteArraySet<Field> fields = new CopyOnWriteArraySet<>();
    private final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201";
    private final String referrer = "http://www.google.com";


    public JsoupParser (Site site, String url) {
        this.site = site;
        this.url = url;
    }

    public Page parseNewPage () {

        Page page = null;
        try {
            response = Jsoup.connect(url).userAgent(userAgent)
                            .referrer(referrer).ignoreHttpErrors(true).execute();

            int code = response.statusCode();
            String content = response.body();
            String path = url.substring(url.indexOf(site.getUrl()) + site.getUrl().length());


            page = new Page(path, code, content, site.getId());

        } catch(IOException ignored) {
        }
        return page;
    }

    public Document setDocument () throws IOException {
        document = response != null ? response.parse()
                : Jsoup.connect(url).userAgent(userAgent).referrer(referrer).get();
        return document;
    }

    public float calculateRank (String lemma) {
        float rank = 0.0f;
        for(Field field : elementsLemmas.keySet()) {
            if(elementsLemmas.get(field).containsKey(lemma)) {
                rank += field.getWeight() * elementsLemmas.get(field).get(lemma);
            }
        }
        return rank;
    }

    public HashMap<Field, HashMap<String, Integer>> findLemmasInElements () {
        elementsLemmas = new HashMap<>();
        Lemmatizator lemmatizator = new Lemmatizator();
        for(Field field : fields) {
            if(document != null) {
                String string = document.select(field.getSelector()).text();
                lemmatizator.setText(string);
                lemmatizator.processText();
            }

            HashMap<String, Integer> lemmas = new HashMap<>(lemmatizator.getLemmaCount());
            elementsLemmas.put(field, lemmas);
        }
        return elementsLemmas;
    }

    public static void setFields (List<Field> fieldList) {
        if(fields.size() == 0) fields.addAll(fieldList);

    }

    public static String getTitle (Page page) {
        return Jsoup.parse(page.getContent()).title();
    }

    public static List<String> getAllStrings (Page page, String searchText) {
        List<String> allStrings = new ArrayList<>();
        Elements elements = Jsoup.parse(page.getContent()).getElementsContainingOwnText(searchText);
        elements.forEach(element -> allStrings.add(element.toString()));
        return allStrings;
    }

    public static CopyOnWriteArraySet<Field> getFields () {
        return fields;
    }
}
