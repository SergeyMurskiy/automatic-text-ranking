package com.murskiy.model;

public class AnalyzedDocumentTokens {
    private String term;
    private int count;

    public String getTerm() {
        return term;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount(){
        count++;
    }

    public AnalyzedDocumentTokens(String term, int count) {
        this.term = term;
        this.count = count;
    }
}
