package com.murskiy.controller;

import com.murskiy.builder.SearchQueryBuilder;
import com.murskiy.model.Tokens;
import com.murskiy.repository.TokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/tokens")
public class SearchTokens {

    @Autowired
    TokensRepository tokensRepository;

    @Autowired
    SearchQueryBuilder searchQueryBuilder;

    @GetMapping(value = "/all")
    public List<Tokens> searchAllTokens() {
        List<Tokens> tokensList = new ArrayList<>();
        Iterable<Tokens> tokens = tokensRepository.findAll();
        tokens.forEach(tokensList::add);
        return tokensList;
    }

    @GetMapping(value = "/remove/all")
    public List<Tokens> removeAllTokens() throws IOException {
        tokensRepository.deleteAll();

        FileWriter writer = new FileWriter("token_id.txt", false);
        writer.write(Integer.toString(1));
        writer.flush();

        return searchAllTokens();
    }

    @GetMapping(value = "/find/{topic}/{token}")
    public List<Tokens> findToken(@PathVariable final String topic, @PathVariable final String token) {
        return searchQueryBuilder.getAll(topic, token);
    }
}
