package com.zxc.interview.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除题库题目关联请求
 */
@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {


    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}