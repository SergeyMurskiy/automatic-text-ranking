package com.murskiy.repository;

import com.murskiy.model.Tokens;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TokensRepository extends ElasticsearchRepository<Tokens, Long> {
}
