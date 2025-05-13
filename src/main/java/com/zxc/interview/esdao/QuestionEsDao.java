package com.zxc.interview.esdao;

import com.zxc.interview.model.dto.question.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 题目 ES 操作
 */
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {
    QuestionEsDTO findByUserId(Long userId);
}
