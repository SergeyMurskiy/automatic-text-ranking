package com.murskiy.model;

import org.springframework.data.annotation.Id;


@org.springframework.data.elasticsearch.annotations.Document(indexName = "course_work", type = "tokens", shards = 1)
public class Tokens {
    @Id
    private long id;
    private String topic;
    private String token;
    private long count;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String name) {
        this.token = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tokens(Long id, String topic, String token, long count) {
        this.id = id;
        this.topic = topic;
        this.token = token;
        this.count = count;
    }
    public Tokens(){};
}

