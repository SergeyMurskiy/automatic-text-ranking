package com.murskiy.controller;

import com.murskiy.model.Tokens;
import com.murskiy.repository.TokensRepository;
import com.murskiy.repository.TopicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/tokens")
public class TokensController {

    @Autowired
    private TokensRepository tokensRepository;

    @Autowired
    private TopicsRepository topicsRepository;

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
        topicsRepository.deleteAll();

        FileWriter writer = new FileWriter("data/token_id.txt", false);
        writer.write(Integer.toString(1));
        writer.flush();

        return searchAllTokens();
    }
}
