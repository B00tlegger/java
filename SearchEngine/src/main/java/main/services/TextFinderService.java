package main.services;

import lombok.Getter;
import main.helpers.JsoupParser;
import main.helpers.Lemmatizator;
import main.model.entityes.Index;
import main.model.entityes.Lemma;
import main.model.entityes.Page;
import main.model.response.FoundRequest;
import main.model.response.Response;
import main.model.entityes.Site;
import main.repositories.IndexRepository;
import main.repositories.LemmaRepository;
import main.repositories.PageRepository;
import main.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TextFinderService {

    private int offset;
    private int limit;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;


    @Autowired
    public TextFinderService (SiteRepository siteRepository, PageRepository pageRepository,
                              IndexRepository indexRepository,
                              LemmaRepository lemmaRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.indexRepository = indexRepository;
        this.lemmaRepository = lemmaRepository;
    }

    public Response search (String query,
                            String site, Integer offset, int limit) {

        this.offset = offset;
        this.limit = limit;
        List<Site> searchSites = new ArrayList<>();
        if(site != null) {
            searchSites.add(siteRepository.findByUrl(site));
        } else {
            searchSites.addAll((Collection<? extends Site>) siteRepository.findAll());
        }
        TextFinder textFinder = new TextFinder(query, searchSites);
        List<FoundRequest> foundPages = textFinder.getFoundPages();


        return new Response(true, textFinder.getIndexes().size(), foundPages);
    }

    public class TextFinder {

        private final List<Site> sites;
        private final String searchText;
        @Getter
        private HashMap<Integer, List<Index>> indexes = new HashMap<>();
        private final Set<Page> pages = new HashSet<>();
        private final Set<Lemma> lemmas = new HashSet<>();
        private final List<String> lemmaList = new ArrayList<>();


        public TextFinder (String searchText, List<Site> sites) {
            this.searchText = searchText;
            this.sites = sites;
            findLemmas();
            findIndexes();
            findPages();
        }

        public List<FoundRequest> getFoundPages () {
            List<FoundRequest> foundPages = new ArrayList<>();
            for(Site site : sites) {
                Set<Page> pagesBySite = pages.stream()
                                             .filter(page -> page.getSiteId() == site.getId())
                                             .collect(Collectors.toSet());
                for(Page page : pagesBySite) {
                    double relevance = getRelativeRelevance(page);
                    String title = getTitle(page);
                    StringBuilder snipped = new StringBuilder();
                    findText(page).forEach(string -> snipped.append("\t").append(string).append("\n"));
                    foundPages.add(new FoundRequest(site.getUrl(),
                                                    site.getName(),
                                                    page.getPath(),
                                                    title,
                                                    snipped.toString(),
                                                    relevance));
                }
            }
            sortCollection(foundPages);
            return foundPages;
        }

        private void findLemmas () {
            Lemmatizator lemmatizator = new Lemmatizator(searchText);
            lemmaList.addAll(lemmatizator.processText());

            lemmas.addAll(lemmaRepository.findLemmasByLemmaInAndSiteIdIn(lemmaList,
                                                                         sites.stream().map(Site::getId)
                                                                              .collect(
                                                                                      Collectors.toList())));

            Set<Integer> sites = lemmas.stream().map(Lemma::getSiteId).collect(Collectors.toSet());
            this.sites.stream()
                      .filter(site -> sites.contains(site.getId()))
                      .collect(Collectors.toSet());
        }

        private void findIndexes () {
            Set<Index> indexList = new HashSet<>(indexRepository.findIndexesByLemmaIdIn(
                    lemmas.stream().map(Lemma::getId).collect(Collectors.toList())));
            Set<Integer> pageId = new HashSet<>();
            for(Index index : indexList) {
                if(pageId.contains(index.getPageId())) {
                    continue;
                }
                List<Index> ind =
                        indexList.stream().filter(i -> i.getPageId() == index.getPageId()).collect(Collectors.toList());
                if(lemmaList.size() == ind.size()) {
                    indexes.put(index.getPageId(), ind);
                }

                pageId.add(index.getPageId());
            }
        }

        private void findPages () {
            List<Integer> pagesId =
                    pageRepository.findPagesByIdInAndContentLike(indexes.keySet(), "%" + searchText + "%");
            HashMap<Integer, List<Index>> ind = new HashMap<>();
            pagesId.forEach(integer -> ind.put(integer, indexes.get(integer)));
            indexes = ind;

            pages.addAll(pageRepository.findPagesByIdIn(pagesId.stream().sorted((o1, o2) -> {
                if(getAbsoluteRank(o1) > getAbsoluteRank(o2)) {
                    return -1;
                }
                if(getAbsoluteRank(o2) < getAbsoluteRank(o1)) {
                    return 1;
                }
                return 0;
            }).skip(offset).limit(limit).collect(Collectors.toList())));
            pages.forEach(page -> {
            });
        }

        private double getAbsoluteRank (int pageId) {
            return indexes.get(pageId).stream().mapToDouble(Index::getRank).sum();
        }

        private double getMaxAbsoluteRank () {
            return indexes.keySet().stream().mapToDouble(this::getAbsoluteRank).max().getAsDouble();
        }

        private Double getRelativeRelevance (Page page) {
            return indexes.keySet().stream().filter(integer -> integer == page.getId())
                          .mapToDouble(integer -> getAbsoluteRank(integer) / getMaxAbsoluteRank()).findAny()
                          .getAsDouble();
        }

        private String getTitle (Page page) {
            return JsoupParser.getTitle(page);
        }

        private Set<String> findText (Page page) {
            return JsoupParser.getAllStrings(page, searchText).stream()
                              .map(s -> s.replaceAll(searchText, "<b>" + searchText + "</b>")).collect(
                            Collectors.toSet());
        }

        private void sortCollection (List<FoundRequest> list) {
            list.sort((foundRequest, foundRequest2) -> {
                if(foundRequest.getRelevance() > foundRequest2.getRelevance()) {
                    return -1;
                } else if(foundRequest2.getRelevance() > foundRequest.getRelevance()) {
                    return 1;
                }
                return 0;
            });
        }
    }
}
