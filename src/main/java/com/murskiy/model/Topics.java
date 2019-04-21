package com.murskiy.model;

import org.springframework.data.annotation.Id;

@org.springframework.data.elasticsearch.annotations.Document(indexName = "course_work", type = "topics", shards = 2)
public class Topics {
    @Id
    private String name;
    private Long analyzedWordsCount;

    public Topics(String name, Long analyzedWordsCount) {
        this.name = name;
        this.analyzedWordsCount = analyzedWordsCount;
    }

    public Topics() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAnalyzedWordsCount() {
        return analyzedWordsCount;
    }

    public void setAnalyzedWordsCount(Long analyzedWordsCount) {
        this.analyzedWordsCount = analyzedWordsCount;
    }

    public void addWorldCount(long n) {
        this.analyzedWordsCount += n;
    }
}
