package com.murskiy.search_service;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class SearchQueryBuilder {
    public NativeSearchQuery getTokensByTermQuery(String term) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .should(
                        QueryBuilders.termQuery("term", term)
                );

        return new NativeSearchQueryBuilder()
                .withQuery(query)
                .build();
    }

    public NativeSearchQuery getTokensByTopicAndTermQuery(String topic, String term) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("topic", topic)
                ).must(
                        QueryBuilders.termQuery("term", term)
                );

        return new NativeSearchQueryBuilder()
                .withQuery(query)
                .build();
    }
}
