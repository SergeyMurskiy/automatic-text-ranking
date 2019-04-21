package com.murskiy.search_service;

import com.murskiy.model.AnalyzedDocumentTokens;
import com.murskiy.model.Tokens;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
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

    public Collection<AnalyzedDocumentTokens> createAnalyzedDocumentTokens(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, AnalyzedDocumentTokens> analyzedTokens = new HashMap<>();

        AnalyzeRequest request = createAnalyzeRequest(file);

        List<AnalyzeResponse.AnalyzeToken> tokens = documentAnalyzer(request);
        System.out.println(tokens.size());

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

        return analyzedTokens.values();
    }

    public Collection<Tokens> createTokens(String topic, MultipartFile file) throws IOException {
        Map<String, Tokens> analyzedTokens = new HashMap<>();

        AnalyzeRequest request = createAnalyzeRequest(file);

        List<AnalyzeResponse.AnalyzeToken> tokens = documentAnalyzer(request);

        long currentTokenId = getCurrentId();
        String term;

        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            term = token.getTerm();
            Tokens saveToken = analyzedTokens.get(term);
            if (saveToken == null) {
                analyzedTokens.put(term, new Tokens(currentTokenId++, topic, term, 1));
            } else {
                saveToken.incrementCount();
            }
        }
        putCurrentId(currentTokenId);

        return analyzedTokens.values();

    }

    private List<AnalyzeResponse.AnalyzeToken> documentAnalyzer(AnalyzeRequest request) {
        return elasticsearchTemplate.getClient().admin().indices().analyze(request).actionGet().getTokens();
    }

    //Кодировка Кирилическая Windows  - "CP1251"
    private AnalyzeRequest createAnalyzeRequest(@RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        AnalyzeRequest request = new AnalyzeRequest();
        request.text(content);
        request.analyzer("russian");
        return request;
    }

    private long getCurrentId() throws IOException{
        Scanner input = new Scanner(new File("data/token_id.txt"));
        return input.nextLong();
    }

    private void putCurrentId(long currentId) throws IOException{
        FileWriter writer = new FileWriter("data/token_id.txt", false);
        writer.write(Long.toString(currentId));
        writer.flush();
    }

}
