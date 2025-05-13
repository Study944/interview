package com.zxc.interview.mapper;

import com.zxc.interview.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 题目表数据库访问层
*/
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("SELECT * FROM question WHERE updatetime >= #{fiveMinutesAgoDate}")
    List<Question> listQuestionWithDelete(Date fiveMinutesAgoDate);
}




