package com.murskiy.model;

import org.springframework.data.annotation.Id;

@org.springframework.data.elasticsearch.annotations.Document(indexName = "course_work", type = "documents", shards = 1)
public class Documents {
    @Id
    private Long id;
    private String topic;
    private String name;
    private String content;

    public Documents(Long id, String topic, String name, String content) {
        this.id = id;
        this.topic = topic;
        this.name = name;
        this.content = content;
    }

    public Documents(){};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}