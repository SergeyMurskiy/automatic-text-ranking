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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping(value = "/documents")
public class SearchDocuments {

    @Autowired
    DocumentsRepository documentsRepository;

    @Autowired
    TokensRepository tokensRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    SearchQueryBuilder searchQueryBuilder;

    @GetMapping(value = "/all")
    public List<Documents> searchAllDocuments() {
        List<Documents> documentsList = new ArrayList<>();
        Iterable<Documents> documents = documentsRepository.findAll();
        documents.forEach(documentsList::add);
        return documentsList;
    }

    @GetMapping(value = "/find/by/name/{name}")
    public List<Documents> searchDocumentByName(@PathVariable final String name) {
        return documentsRepository.findByName(name);
    }

    @GetMapping(value = "/find/by/topic/{topic}")
    public List<Documents> searchDocumentByTopic(@PathVariable final String topic) {
        return documentsRepository.findByTopic(topic);
    }

    @GetMapping(value = "/remove/by/id/{id}")
    public List<Documents> deleteDocumentById(@PathVariable final long id) {
        documentsRepository.deleteById(id);
        return searchAllDocuments();
    }

    @GetMapping(value = "/remove/all")
    public List<Documents> removeAllDocuments() throws IOException{
        documentsRepository.deleteAll();

        FileWriter writer = new FileWriter("doc_id.txt", false);
        writer.write(Long.toString(1));
        writer.flush();

        return searchAllDocuments();
    }

    @GetMapping(value = "/add/{topic}/{path}")
    public List<Documents> addDocument(@PathVariable final String topic, @PathVariable final String path) throws IOException {
        String FilePath = "file_to_upload/" + path + ".txt";
        File file = new File(FilePath);
        String name = file.getName();
        String[] str = name.split("\\.");
        BufferedReader reader = new BufferedReader(new FileReader(FilePath));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } finally {
            reader.close();
        }

        Scanner input = new Scanner(new File("doc_id.txt"));
        long currentId = input.nextLong();
        Documents doc = new Documents(currentId++, topic, str[0], stringBuilder.toString());
        FileWriter writer = new FileWriter("doc_id.txt", false);
        writer.write(Long.toString(currentId));
        writer.flush();

        documentsRepository.save(doc);

        return searchAllDocuments();
    }

    @GetMapping(value = "/analyze/{id}")
    public List<Documents> getAll(@PathVariable final long id) throws IOException{

        List<Documents> documentsList = documentsRepository.findById(id);
        AnalyzeRequest request = new AnalyzeRequest();
        request.text(documentsList.get(0).getContent());
        request.analyzer("russian");
        List<AnalyzeResponse.AnalyzeToken> tokens = elasticsearchTemplate.getClient().admin().indices().analyze(request).actionGet().getTokens();

        String topic = documentsList.get(0).getTopic();
        String term;
        List<Tokens> checkToken;
        Scanner input = new Scanner(new File("token_id.txt"));
        int currentId = input.nextInt();

        for (int i = 0; i < tokens.size(); i++) {
            term = tokens.get(i).getTerm();
             checkToken = searchQueryBuilder.getAll(topic, term);
            if (checkToken.isEmpty()) {
                Tokens newToken = new Tokens((long)currentId++, topic, term, 1);
                tokensRepository.save(newToken);
            } else {
                checkToken.get(0).setCount(checkToken.get(0).getCount() + 1);
                tokensRepository.save(checkToken);
            }
        }

        FileWriter writer = new FileWriter("token_id.txt", false);
        writer.write(Integer.toString(currentId));
        writer.flush();

        return searchAllDocuments();
    }
}
