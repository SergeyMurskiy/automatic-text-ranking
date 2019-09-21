package com.murskiy.controller;

import com.murskiy.model.SearchResponse;
import com.murskiy.model.Tokens;
import com.murskiy.model.Topics;
import com.murskiy.repository.TokensRepository;
import com.murskiy.repository.TopicsRepository;
import com.murskiy.search_service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/learning")
public class LearningController {
    @Autowired
    private SearchService searchService;

    @Autowired
    private TokensRepository tokensRepository;

    @Autowired
    private TopicsRepository topicsRepository;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String learningAllFiles(@RequestParam("topic") String topic,
                                   @RequestParam("files") MultipartFile[] files) throws IOException {

        long start = System.currentTimeMillis();

        Map<String, Tokens> tokensToSave = new HashMap<>();
       // ArrayList<Tokens> tokensToSave = new ArrayList<>();

        long analyzedWordsCount = 0;

        for (MultipartFile file : files) {
            SearchResponse<Tokens> analyzedTokens = searchService.createTokens(topic, file);
            analyzedWordsCount += analyzedTokens.getWordsCount();

            for (Tokens analyzedToken : analyzedTokens.getTokens()) {
                String term = analyzedToken.getTerm();

                Tokens checkToken = tokensToSave.get(term);
                if (checkToken == null) {
                    tokensToSave.put(term, analyzedToken);
                } else {
                    checkToken.setCount(checkToken.getCount() + analyzedToken.getCount());
                }
            }
            //tokensToSave.addAll(getNewTokens(analyzedTokens.getTokens(), topic));
        }

        Topics learningTopic = topicsRepository.findByName(topic);

        if (learningTopic == null) {
            learningTopic = new Topics(topic, analyzedWordsCount);
        } else {
            learningTopic.addWorldCount(analyzedWordsCount);
        }

        topicsRepository.save(learningTopic);
        tokensRepository.save(tokensToSave.values());

        long end = System.currentTimeMillis();

        return response((end - start));
    }

    private ArrayList<Tokens> getNewTokens(Collection<Tokens> analyzedTokens, String topic) {
        ArrayList<Tokens> newTokens = new ArrayList<>();

        for (Tokens analyzedToken : analyzedTokens) {
            String term = analyzedToken.getTerm();

            List<Tokens> checkToken = searchService.getTokenByTopicAndTerm(topic, term);
            if (checkToken.isEmpty()) {
                newTokens.add(analyzedToken);
            } else {
                Tokens existToken = checkToken.get(0);
                existToken.addCount(analyzedToken.getCount());
                newTokens.add(existToken);
            }
        }
        return newTokens;
    }

    private String response(long time) {
        return "Success! " + time + "нc.";
    }
}
