package com.murskiy.repository;

import com.murskiy.model.Tokens;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TokensRepository extends ElasticsearchRepository<Tokens, Long> {
    List<Tokens> findByToken(String token);
}
