package com.murskiy.controller;

import com.murskiy.model.AnalyzedDocumentTokens;
import com.murskiy.model.Tokens;
import com.murskiy.model.Topics;
import com.murskiy.repository.TopicsRepository;
import com.murskiy.search_service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/analyze")
public class AnalyzeController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private TopicsRepository topicsRepository;

    private long documentsWordsCount;
    private Map<String, Long> topicsWordCount;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String analyze(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "Пустой файл";
        }

        long start = System.currentTimeMillis();

        Collection<AnalyzedDocumentTokens> analyzedTokens = searchService.createAnalyzedDocumentTokens(file);

        this.documentsWordsCount = analyzedTokens.size();
        this.topicsWordCount = getTopicsWordCount();

        Collection<ResponseElement> result = getResult(analyzedTokens);

        long end = System.currentTimeMillis();

        return response(result, (end - start), analyzedTokens.size());
    }

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public String analyzeAll(@RequestParam("files") MultipartFile[] files) throws IOException {
        StringBuilder res = new StringBuilder();
        for (MultipartFile file: files) {
            res.append(analyze(file));
        }

        return res.toString();

    }

    private Map<String, Long> getTopicsWordCount() {
        Iterable<Topics> tmp = topicsRepository.findAll();
        Map<String, Long> topicsMap = new HashMap<>();
        for (Topics topic : tmp) {
            topicsMap.put(topic.getName(), topic.getAnalyzedWordsCount());
        }

        return topicsMap;
    }

    private String getActualTopic(AnalyzedDocumentTokens token, List<Tokens> findTokens) {
        String topic = findTokens.get(0).getTopic();

        double minValue = Math.abs((double) token.getCount() / this.documentsWordsCount - (double) findTokens.get(0).getCount() / this.topicsWordCount.get(topic));

        for (Tokens findToken : findTokens) {
            double value = Math.abs((double) token.getCount() / this.documentsWordsCount - (double) findToken.getCount() / this.topicsWordCount.get(findToken.getTopic()));
            System.out.println(minValue);
            if (value <= minValue) {
                minValue = value;
                topic = findToken.getTopic();
            }
        }

        return topic;
    }

    private Collection<AnalyzeController.ResponseElement> getResult(Collection<AnalyzedDocumentTokens> analyzedTokens) {

        Map<String, ResponseElement> resultTopicsCount = new HashMap<>();

        for (AnalyzedDocumentTokens token : analyzedTokens) {

            List<Tokens> findTokens = searchService.getTokensByTerm(token.getTerm());
            if (findTokens.isEmpty()) {
                continue;
            }

            String topic = getActualTopic(token, findTokens);

            ResponseElement actualElement = resultTopicsCount.get(topic);

            if (actualElement == null) {
                resultTopicsCount.put(topic, new ResponseElement(topic, 1));
            } else {
                actualElement.incrementCount();
            }
        }

        return resultTopicsCount.values();
    }

    private String response(Collection<ResponseElement> result, long time, long sizeOfAnalyzedTokens) {

        StringBuilder response = new StringBuilder(
                "Токенов в анализируемом документе:   " + sizeOfAnalyzedTokens + "\n" +
                "Время:  " + time + " нс.\n" +
                "Результаты: \n" );
        for (ResponseElement row : result) {
            response.append("\n").append(row.topic).append(" ").append(row.count);
        }
        response.append("\n_____________________________________________________________________________________________\n\n");
        return response.toString();
    }

    private class ResponseElement {
        private String topic;
        private int count;

        public ResponseElement(String topic, int count) {
            this.topic = topic;
            this.count = count;
        }

        public void incrementCount() {
            this.count++;
        }
    }
}
