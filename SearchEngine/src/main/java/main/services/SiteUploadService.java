package main.services;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import main.properties.AppProp;
import main.helpers.JsoupParser;
import main.helpers.Lemmatizator;
import main.model.entityes.*;
import main.model.response.Response;
import main.model.response.Statistic;
import main.repositories.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@Service
public class SiteUploadService {

    private final AppProp appProp;
    private boolean indexing;
    private List<SiteUploader> threads;
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();


    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;

    @Autowired
    public SiteUploadService (SiteRepository siteRepository,
                              PageRepository pageRepository,
                              IndexRepository indexRepository,
                              LemmaRepository lemmaRepository,
                              FieldRepository fieldRepository,
                              AppProp appProp) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.indexRepository = indexRepository;
        this.lemmaRepository = lemmaRepository;
        this.fieldRepository = fieldRepository;
        this.indexing = false;
        this.appProp = appProp;
    }

    public Response startIndexing () {
        parsFields();
        if(indexing) return new Response(false, "Индексация уже запущена");

        Thread thread = new Thread(() -> {
            try {
                processIndexing();
            } catch(IOException e) {
                indexing = false;
            }

        });
        thread.start();
        return new Response(true);
    }

    public Response statistics () {
        List<Statistic.DetailedStatistic> detailedStatistics = new ArrayList<>();
        List<Site> sites = (List<Site>) siteRepository.findAll();
        for(Site site : sites) {
            detailedStatistics.add(new Statistic.DetailedStatistic(site.getUrl(),
                                                                   site.getName(),
                                                                   site.getStatus(),
                                                                   site.getStatusTime(),
                                                                   site.getLastErrorTxt() == null ?
                                                                           "Ошибок не обнаружено" :
                                                                           site.getLastErrorTxt(),
                                                                   pageRepository.countBySiteId(site.getId()),
                                                                   lemmaRepository.countBySiteId(site.getId())));
        }
        return new Response(
                true,
                new Statistic(
                        new Statistic.TotalStatistic(
                                sites.size(),
                                pageRepository.count(),
                                lemmaRepository.count()),
                        detailedStatistics));
    }

    public Response stopIndexing () {
        if(!indexing) return new Response(false, "Индексация не запущена");

        if(threads != null || threads.size() != 0) {
            for(SiteUploader siteUploader : threads) {
                if(siteUploader.isAlive()) {
                    siteUploader.finish();

                }
            }

        }
        indexing = false;
        return new Response(true);
    }

    public Response indexPage (String url) {
        PageRefresher pageRefresher =
                new PageRefresher(siteRepository, pageRepository, lemmaRepository, indexRepository, url);
        if(pageRefresher.getSite() == null) {
            return new Response(false,
                                "Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }

        parsFields();
        pageRefresher.refreshPage();
        return new Response(true);
    }

    private void parsFields () {
        if(JsoupParser.getFields() != null || JsoupParser.getFields().size() == 0) {
            JsoupParser.setFields((List<Field>) fieldRepository.findAll());
        }
    }

    private void processIndexing () throws IOException {
        indexing = true;
        threads = new ArrayList<>();
        for(Site site : appProp.getSites()) {
            Site foundSite = siteRepository.findByUrl(site.getUrl());

            if(foundSite != null) {
                site = foundSite;
                site.setStatus(Site.StatusType.INDEXING);
                site = siteRepository.save(site);
                clear(site);
            } else {
                site.setStatus(Site.StatusType.INDEXING);
                site = siteRepository.save(site);
            }

            threads.add(new SiteUploader(site));
        }
        threads.forEach(Thread::start);
        threads.forEach(siteUploader -> {
            try {
                siteUploader.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        indexing = false;
    }

    private void clear (Site site) {
        lemmaRepository.removeBySiteId(site.getId());
        indexRepository.deleteAllIndexesByPageIdIn(
                pageRepository.findPagesBySiteId(site.getId()).stream().map(Page::getId).collect(
                        Collectors.toList()));
        pageRepository.removeBySiteId(site.getId());
    }

    private class SiteUploader extends Thread {

        private final String url;
        private final Site site;
        private HashMap<Integer, HashMap<String, Float>> pages;
        private boolean isAlive = true;

        private SiteUploader (Site site) {
            this.url = site.getUrl() + "/";
            this.site = site;
        }

        @Override
        public void run () {
            try {
                scan();
                uploadLemmas(lemmasCalculator());
                uploadPages();
                if(isAlive) {
                    siteRepository.save(editSite(null, Site.StatusType.INDEXED));
                } else {
                    siteRepository.save(editSite("Сканирование остановленно", Site.StatusType.FAILED));
                }

            } catch(Exception e) {
                siteRepository.save(editSite(e.getMessage(), Site.StatusType.FAILED));
            }
        }

        private void uploadPages () {
            HashMap<String, Integer> lemmas = new HashMap<>();
            List<Index> indexList = new ArrayList<>();
            for(Lemma lemma : lemmaRepository.findLemmasBySiteId(site.getId())) {
                lemmas.put(lemma.getLemma(), lemma.getId());
            }
            for(Integer integer : pages.keySet()) {
                for(String s : pages.get(integer).keySet()) {
                    if(lemmas.containsKey(s)) {
                        indexList.add(new Index(integer, lemmas.get(s), pages.get(integer).get(s)));
                    }
                }
            }
            indexRepository.saveIndexes(indexList);
        }

        private void uploadLemmas (HashMap<String, Integer> frequency) {

            Set<Lemma> allLemmas = new HashSet<>();

            for(
                    String lemma : frequency.keySet()) {
                allLemmas.add(new Lemma(lemma, frequency.get(lemma), site.getId()));
            }
            lemmaRepository.saveLemmas(allLemmas);
        }

        private void scan () throws Exception {
            SiteScanner siteScanner = new SiteScanner(site, url, new CopyOnWriteArraySet<>());
            pages = forkJoinPool.invoke(siteScanner);
            if(pages.size() == 0) {
                throw new IOException("Главная страница недоступна");
            }
        }


        private HashMap<String, Integer> lemmasCalculator () {
            HashMap<String, Integer> frequency = new HashMap<>();
            for(Integer pageId : pages.keySet()) {
                for(String s : pages.get(pageId).keySet()) {
                    int count = frequency.getOrDefault(s, 0);
                    frequency.put(s, count + 1);
                }
            }

            return frequency;
        }

        private Site editSite (String error, Site.StatusType statusType) {
            site.setLastErrorTxt(error);
            site.setStatusTime(System.currentTimeMillis());
            site.setStatus(statusType);
            return site;
        }

        private void finish () {
            isAlive = false;
        }

    }

    private class SiteScanner extends RecursiveTask<HashMap<Integer, HashMap<String, Float>>> {

        private final String url;
        private final Site site;
        @Getter
        @Setter
        private CopyOnWriteArraySet<String> allLinks;
        private Set<SiteScanner> linksFinders;

        private SiteScanner (Site site, String url, CopyOnWriteArraySet<String> allLinks) {
            this.site = site;
            this.url = url;
            this.allLinks = allLinks;
        }

        @Override
        protected HashMap<Integer, HashMap<String, Float>> compute () {
            HashMap<Integer, HashMap<String, Float>> siteInfo = new HashMap<>();
            try {
                linksFinders = new HashSet<>();

                if(!allLinks.contains(url) && indexing) {
                    allLinks.add(url);

                    PageScanner pageScanner = new PageScanner(site, url);
                    pageScanner.scan();
                    int pageId = pageRepository.save(pageScanner.getPage()).getId();
                    getChildren(pageScanner.getDocument());
                    siteInfo.put(pageId, pageScanner.getLemmasAndRank());
                }
            } catch(Exception ignored) {
            }
            if(indexing) {
                for(SiteScanner siteScanner : linksFinders) {
                    siteInfo.putAll(siteScanner.join());
                }
            }
            return siteInfo;
        }

        private void getChildren (Document document) {
            Elements elements;
            elements = document.getElementsByTag("a");
            for(Element element : elements) {
                String linkUrl = element.attr("abs:href").trim();
                if(!linkUrl.isEmpty() && linkUrl.startsWith(site.getUrl()) && !linkUrl.contains("#") &&
                        !allLinks.contains(linkUrl)) {
                    SiteScanner siteScanner = new SiteScanner(site, linkUrl, allLinks);
                    siteScanner.fork();
                    linksFinders.add(siteScanner);
                }
            }
        }
    }

    public class PageRefresher {
        private final SiteRepository siteRepository;
        private final PageRepository pageRepository;
        private final LemmaRepository lemmaRepository;
        private final IndexRepository indexRepository;


        @Getter
        private Site site;
        private final String url;
        private Page page;
        Set<Lemma> oldPageLemmas = null;


        public PageRefresher (SiteRepository siteRepository,
                              PageRepository pageRepository,
                              LemmaRepository lemmaRepository,
                              IndexRepository indexRepository,
                              String url) {
            this.siteRepository = siteRepository;
            this.pageRepository = pageRepository;
            this.lemmaRepository = lemmaRepository;
            this.indexRepository = indexRepository;
            this.url = url;
            setSite();
            setPage();
        }

        public void setSite () {
            for(Site s : siteRepository.findAll()) {
                if(url.startsWith(s.getUrl())) {
                    site = siteRepository.findByUrl(s.getUrl());
                    break;
                }
            }
        }

        public void setPage () {
            String path = url.substring(url.indexOf(site.getUrl()) + site.getUrl().length());
            page = pageRepository.findPageByPathAndSiteId(path, site.getId());
        }

        public void refreshPage () {
            PageScanner pageScanner;
            if(page != null) {
                pageScanner = new PageScanner(site, page);
                pageScanner.scan();
            } else {
                pageScanner = new PageScanner(site, url);
                pageScanner.scan();
                page = pageRepository.save(pageScanner.getPage());
            }
            Set<String> foundLemmas = new HashSet<>(pageScanner.findLemmas());
            addNewLemmas(foundLemmas);
            Set<Lemma> pagesLemmas =
                    new HashSet<>(
                            lemmaRepository.findLemmasByLemmaInAndSiteId(new ArrayList<>(foundLemmas), site.getId()));
            editOldLemmas(pagesLemmas);
            indexing(pageScanner.getLemmasAndRank(), pagesLemmas);
        }

        private void editOldLemmas (Set<Lemma> pagesLemmas) {
            Set<Lemma> oldLemmas = new HashSet<>(lemmaRepository.findLemmasByPageId(page.getId()));
            Set<Lemma> lemmasForUpdate =
                    pagesLemmas.stream().filter(lemma -> !oldLemmas.contains(lemma)).collect(Collectors.toSet());
            if(lemmasForUpdate.size() > 0) lemmaRepository.setLemmas(lemmasForUpdate, "-1");
        }

        private void addNewLemmas (Set<String> foundLemmas) {
            Set<Lemma> pagesLemmas =
                    new HashSet<>(
                            lemmaRepository.findLemmasByLemmaInAndSiteId(new ArrayList<>(foundLemmas), site.getId()));
            oldPageLemmas = new HashSet<>(lemmaRepository.findLemmasByPageId(page.getId()));
            if(pagesLemmas.size() > oldPageLemmas.size()) {
                Set<Lemma> lemmasToUpdate =
                        pagesLemmas.stream().filter(lemma -> !oldPageLemmas.contains(lemma))
                                   .collect(Collectors.toSet());
                lemmaRepository.setLemmas(lemmasToUpdate, "+1");
            }
            if(pagesLemmas.size() != foundLemmas.size()) {
                Set<String> lemmas = pagesLemmas.stream().map(Lemma::getLemma).collect(Collectors.toSet());
                foundLemmas.stream().filter(s -> !lemmas.contains(s))
                           .forEach(s -> pagesLemmas.add(new Lemma(s, 1, site.getId())));
            }
        }

        private void indexing (HashMap<String, Float> lemmasAndRank, Set<Lemma> pagesLemmas) {
            indexRepository.deleteByPageId(page.getId());
            List<Index> indexes = pagesLemmas.stream()
                                             .map(lemma -> new Index(page.getId(), lemma.getId(),
                                                                     lemmasAndRank.get(lemma.getLemma())))
                                             .collect(Collectors.toList());
            indexRepository.saveIndexes(indexes);
        }
    }

    @Data
    private class PageScanner {
        private Page page;
        private Site site;
        private String url;
        private Document document;
        private final HashMap<String, Float> lemmasAndRank = new HashMap<>();
        private JsoupParser jsoupParser;

        private PageScanner (Site site, String url) {
            this.site = site;
            this.url = url;
        }

        private PageScanner (Site site, Page page) {
            this.site = site;
            this.page = page;
            url = site.getUrl() + page.getPath();
        }

        private void scan () {
            if(jsoupParser == null) {
                jsoupParser = new JsoupParser(site, url);
            }
            if(page == null) {
                setPage();
            }
            if(page != null) {
                try {
                    setDocument();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(document != null) {
                    findAllLemmasAndRank();
                }
            }
        }


        private void setPage () {
            if(jsoupParser == null) {
                jsoupParser = new JsoupParser(site, url);
            }
            page = jsoupParser.parseNewPage();
        }

        private void setDocument () throws IOException {
            if(page.getCode() < 400) {
                document = jsoupParser.setDocument();
            }
        }

        private Set<String> findLemmas () {
            Lemmatizator lemmatizator = new Lemmatizator(document.text());
            return new HashSet<>(lemmatizator.processText());
        }

        private void findAllLemmasAndRank () {
            HashMap<Field, HashMap<String, Integer>> fields = jsoupParser.findLemmasInElements();
            Set<String> pagesLemmas = new HashSet<>();
            for(Field field : fields.keySet()) {
                pagesLemmas.addAll(fields.get(field).keySet());
            }
            for(String string : pagesLemmas) {
                float rank = jsoupParser.calculateRank(string);
                if(rank > 0) {
                    lemmasAndRank.put(string, rank);
                }
            }
        }
    }
}
