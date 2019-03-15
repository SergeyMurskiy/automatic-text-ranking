package com.murskiy.builder;

import com.murskiy.model.Tokens;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchQueryBuilder {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List<Tokens> getAll(String topic, String token) {
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("topic", topic)
                ).must(
                        QueryBuilders.termQuery("token", token));
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(query)
                .build();

        List<Tokens> tokens = elasticsearchTemplate.queryForList(build, Tokens.class);

        return tokens;
    }
}
