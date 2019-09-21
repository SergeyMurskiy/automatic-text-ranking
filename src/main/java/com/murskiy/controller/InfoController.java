package com.murskiy.controller;

import com.murskiy.model.Tokens;
import com.murskiy.model.Topics;
import com.murskiy.repository.TokensRepository;
import com.murskiy.repository.TopicsRepository;
import com.murskiy.search_service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class InfoController {
    @Autowired
    private TokensRepository tokensRepository;

    @Autowired
    private TopicsRepository topicsRepository;

    @Autowired
    private SearchService searchService;

    @GetMapping(value = "tokens/all")
    public List<Tokens> searchAllTokens() {
        List<Tokens> tokensList = new ArrayList<>();
        Iterable<Tokens> tokens = tokensRepository.findAll();
        tokens.forEach(tokensList::add);
        return tokensList;
    }

    @GetMapping(value = "tokens/remove/all")
    public List<Tokens> removeAllTokens() {
        tokensRepository.deleteAll();
        topicsRepository.deleteAll();

        return searchAllTokens();
    }

    @GetMapping(value = "tokens/term/{term}")
    public List<Tokens> searchTokenByTerm(@PathVariable String term) {
        return searchService.getTokensByTerm(term);
    }


    @GetMapping(value = "topics/all")
    public List<Topics> all() {
        List<Topics> topicList = new ArrayList<>();
        Iterable<Topics> topics = topicsRepository.findAll();
        topics.forEach(topicList::add);
        return topicList;
    }
}
