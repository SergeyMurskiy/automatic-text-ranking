package com.murskiy.model;

import org.springframework.data.annotation.Id;


@org.springframework.data.elasticsearch.annotations.Document(indexName = "course_work", type = "tokens", shards = 1)
public class Tokens {
    @Id
    private long id;
    private String topic;
    private String term;
    private long count;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String name) {
        this.term = name;
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

    public Tokens(Long id, String topic, String term, long count) {
        this.id = id;
        this.topic = topic;
        this.term = term;
        this.count = count;
    }
    public Tokens(){}

    public void incrementCount() {
        this.count++;
    }

    public void addCount(long n) {
        this.count += n;
    }

}

