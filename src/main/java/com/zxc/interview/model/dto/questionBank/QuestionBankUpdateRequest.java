package com.zxc.interview.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新题库请求(用户)
 */
@Data
public class QuestionBankUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}