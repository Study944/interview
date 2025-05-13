package com.zxc.interview.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.zxc.interview.esdao.QuestionEsDao;
import com.zxc.interview.mapper.QuestionMapper;
import com.zxc.interview.model.dto.question.QuestionEsDTO;
import com.zxc.interview.model.entity.Question;
import com.zxc.interview.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步题目到 es
 */
@Component
@Slf4j
public class IncSyncQuestionToEs{

    @Resource
    private QuestionEsDao questionEsDao;
    @Resource
    private QuestionMapper questionMapper;

    @Scheduled(fixedDelay = 60 * 1000)
    public void run() {
        // 1.查询5分钟内更新题目
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<Question> questionList = questionMapper.listQuestionWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questionList)) {
            log.info("no inc post");
            return;
        }
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
