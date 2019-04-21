package com.murskiy.controller;

import com.murskiy.model.Topics;
import com.murskiy.repository.TopicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/topics")
public class TopicsController {

    @Autowired
    private TopicsRepository topicsRepository;

    @GetMapping(value = "/all")
    public List<Topics> all() {
        List<Topics> topicList = new ArrayList<>();
        Iterable<Topics> topics = topicsRepository.findAll();
        topics.forEach(topicList::add);
        return topicList;
    }
}
