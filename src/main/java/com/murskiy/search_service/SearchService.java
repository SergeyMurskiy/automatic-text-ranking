package com.murskiy.search_service;

import com.murskiy.model.AnalyzedDocumentTokens;
import com.murskiy.model.SearchResponse;
import com.murskiy.model.Tokens;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Component
public class SearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SearchQueryBuilder searchQueryBuilder;

    public List<Tokens> getTokensByTerm(String term) {
        return elasticsearchTemplate.queryForList(searchQueryBuilder
                .getTokensByTermQuery(term), Tokens.class);
    }

    public List<Tokens> getTokenByTopicAndTerm(String topic, String term) {
        return elasticsearchTemplate.queryForList(searchQueryBuilder
                .getTokensByTopicAndTermQuery(topic, term), Tokens.class);
    }

    public SearchResponse<AnalyzedDocumentTokens> createAnalyzedDocumentTokens(MultipartFile file) throws IOException {
        Map<String, AnalyzedDocumentTokens> analyzedTokens = new HashMap<>();

        AnalyzeRequest request = createAnalyzeRequest(file);

        List<AnalyzeResponse.AnalyzeToken> tokens = documentAnalyzer(request);

        String term;

        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            term = token.getTerm();
            AnalyzedDocumentTokens saveToken = analyzedTokens.get(term);
            if (saveToken == null) {
                analyzedTokens.put(term, new AnalyzedDocumentTokens(term, 1));
            } else {
                saveToken.incrementCount();
            }
        }

        return new SearchResponse<>(tokens.size(), analyzedTokens.values());
    }

    public SearchResponse<Tokens> createTokens(String topic, MultipartFile file) throws IOException {
        Map<String, Tokens> analyzedTokens = new HashMap<>();

        AnalyzeRequest request = createAnalyzeRequest(file);

        List<AnalyzeResponse.AnalyzeToken> tokens = documentAnalyzer(request);

        String term;

        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            term = token.getTerm();
            Tokens saveToken = analyzedTokens.get(term);
            if (saveToken == null) {
                analyzedTokens.put(term, new Tokens(generateId(topic, term), topic, term, 1));
            } else {
                saveToken.incrementCount();
            }
        }

        return new SearchResponse<>(tokens.size(), analyzedTokens.values());

    }

    private List<AnalyzeResponse.AnalyzeToken> documentAnalyzer(AnalyzeRequest request) {
        return elasticsearchTemplate.getClient().admin().indices().analyze(request).actionGet().getTokens();
    }

    //Кодировка Кирилическая Windows  - "CP1251"
    private AnalyzeRequest createAnalyzeRequest(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), "CP1251");
        AnalyzeRequest request = new AnalyzeRequest();
        request.text(content);
        request.analyzer("russian");
        return request;
    }

    private long generateId(String topic, String term) {
        return topic.concat(term).hashCode();
    }
}
