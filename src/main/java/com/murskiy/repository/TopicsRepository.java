package com.murskiy.repository;

import com.murskiy.model.Topics;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TopicsRepository extends ElasticsearchRepository<Topics, String> {
    Topics findByName(String name);
}
