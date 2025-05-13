package com.zxc.interview.job.once;

import com.zxc.interview.esdao.QuestionEsDao;
import com.zxc.interview.model.dto.question.QuestionEsDTO;
import com.zxc.interview.model.entity.Question;
import com.zxc.interview.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步题目到 es
 */
@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {

    @Resource
    private QuestionEsDao questionEsDao;
    @Resource
    private QuestionService questionService;

    @Override
    public void run(String... args) throws Exception {
        // 1.查询全部题目
        List<Question> questionList = questionService.list();
        List<QuestionEsDTO> questionEsDTOList = questionList
                .stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        // 2.分页写入ES
        final int pageSize = 500;
        int maxSize = questionEsDTOList.size();
        log.info("FullSyncQuestionToEs start, total {}", maxSize);
        for (int i = 0; i < maxSize; i += pageSize) {
            int end = Math.min(i + pageSize, maxSize);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
    }
}
