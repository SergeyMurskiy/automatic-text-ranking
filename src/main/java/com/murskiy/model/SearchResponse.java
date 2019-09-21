package com.murskiy.model;

import java.util.Collection;

public class SearchResponse<T> {
    private int wordsCount;
    private Collection<T> tokens;

    public SearchResponse(int wordsCount, Collection<T> tokens) {
        this.wordsCount = wordsCount;
        this.tokens = tokens;
    }

    public int getWordsCount() {
        return wordsCount;
    }

    public Collection<T> getTokens() {
        return tokens;
    }
}
