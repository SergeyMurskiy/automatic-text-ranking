package com.murskiy.controller;

import com.murskiy.builder.SearchQueryBuilder;
import com.murskiy.model.Documents;
import com.murskiy.model.Tokens;
import com.murskiy.repository.DocumentsRepository;
import com.murskiy.repository.TokensRepository;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping(value = "/learning")
public class LearningController {
    @Autowired
    TokensRepository tokensRepository;

    @Autowired
    SearchQueryBuilder searchQueryBuilder;

    @Autowired
    DocumentsRepository documentsRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @GetMapping(value = "/all/{topic}")
    public String getAllFiles(@PathVariable final String topic) throws IOException {
        File dir = new File("file_to_upload");
        File[] listOfFiles = dir.listFiles();

        String nameOfFile;
        String pathToFile;
        String documentContent;
        long currentDocId = getCurrentId("document");
        long currentTokenId = getCurrentId("token");

        AnalyzeRequest request;
        List<AnalyzeResponse.AnalyzeToken> tokens;

        for (int i = 0; i < listOfFiles.length; i++) {
            nameOfFile = listOfFiles[i].getName();
            if (nameOfFile.equals(".DS_Store")) {
                continue;
            }
            pathToFile = "file_to_upload/" + nameOfFile;
            documentContent = getDocumentsContent(pathToFile);
            Documents newDocument = new Documents(currentDocId++, topic, nameOfFile.split("\\.")[0], documentContent);
            documentsRepository.save(newDocument);

            request = new AnalyzeRequest();
            request.text(documentContent);
            request.analyzer("russian");
            tokens = elasticsearchTemplate.getClient().admin().indices().analyze(request).actionGet().getTokens();
            String term;
            List<Tokens> checkToken;
            for (int j = 0; j < tokens.size(); j++) {
                term = tokens.get(j).getTerm();
                checkToken = searchQueryBuilder.getAll(topic, term);
                if (checkToken.isEmpty()) {
                    Tokens newToken = new Tokens(currentTokenId++, topic, term, 1);
                    tokensRepository.save(newToken);
                } else {
                    checkToken.get(0).setCount(checkToken.get(0).getCount() + 1);
                    tokensRepository.save(checkToken);
                }
            }
        }

        putCurrentId("document" , currentDocId);
        putCurrentId("token", currentTokenId);
        return "Success!";
    }

    private String getDocumentsContent(String pathToFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
        String line = null;
        String ls = System.getProperty("line.separator");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } finally {
            reader.close();
        }
        return stringBuilder.toString();
    }

    private long getCurrentId(String type) throws IOException{
        if (type.equals("token")) {
            Scanner input = new Scanner(new File("token_id.txt"));
            return input.nextLong();
        } else if (type.equals("document")) {
            Scanner input = new Scanner(new File("doc_id.txt"));
            return input.nextLong();
        }
        return 0;
    }

    private void putCurrentId(String type, long currentId) throws IOException{
        if (type.equals("token")) {

            FileWriter writer = new FileWriter("token_id.txt", false);
            writer.write(Long.toString(currentId));
            writer.flush();

        } else if (type.equals("document")) {
            FileWriter writer = new FileWriter("doc_id.txt", false);
            writer.write(Long.toString(currentId));
            writer.flush();
        }
    }
}
