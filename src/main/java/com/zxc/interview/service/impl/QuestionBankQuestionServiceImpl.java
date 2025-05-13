package com.zxc.interview.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxc.interview.common.ErrorCode;
import com.zxc.interview.constant.CommonConstant;
import com.zxc.interview.exception.BusinessException;
import com.zxc.interview.exception.ThrowUtils;
import com.zxc.interview.mapper.QuestionBankQuestionMapper;
import com.zxc.interview.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.zxc.interview.model.entity.Question;
import com.zxc.interview.model.entity.QuestionBank;
import com.zxc.interview.model.entity.QuestionBankQuestion;
import com.zxc.interview.model.entity.User;
import com.zxc.interview.model.vo.QuestionBankQuestionVO;
import com.zxc.interview.model.vo.UserVO;
import com.zxc.interview.service.QuestionBankQuestionService;
import com.zxc.interview.service.QuestionBankService;
import com.zxc.interview.service.QuestionService;
import com.zxc.interview.service.UserService;
import com.zxc.interview.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankService questionBankService;

    @Lazy
    @Resource
    private QuestionService questionService;

    /**
     * 校验数据
     * @param questionBankQuestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        Long questionId = questionBankQuestion.getQuestionId();
        // 对题库和题目进行验证是否存在
        if (questionBankId!= null && questionBankId > 0){
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
        }
        if (questionId!= null && questionId > 0){
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        }
    }

    /**
     * 获取查询条件
     * @param questionBankQuestionQueryRequest
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String title = questionBankQuestionQueryRequest.getTitle();
        String content = questionBankQuestionQueryRequest.getContent();
        String searchText = questionBankQuestionQueryRequest.getSearchText();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList
                .stream()
                .map(QuestionBankQuestionVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList
                .stream()
                .map(QuestionBankQuestion::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService
                .listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量添加题目到题库
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    @Override
    public void batchAddQuestionsToBank(List<Long> questionIdList, Long questionBankId, User loginUser){
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 1. 判断题目和题库是否存在
        // 提高代码性能，只查询需要的列，尽量走覆盖索引
        LambdaQueryWrapper<Question> wrapper1 = Wrappers.lambdaQuery(Question.class)
                .in(Question::getId, questionIdList)
                .select(Question::getId);
        List<Long> questionList = questionService.listObjs(wrapper1, o -> (Long) o);
        List<Long> questionIds = questionList.stream()
                .filter(q -> q != null)
                .collect(Collectors.toList());
        ThrowUtils.throwIf(questionIds==null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank==null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        // 提高代码健壮性，提前校验题库中是否已存在题目
        LambdaQueryWrapper<QuestionBankQuestion> existQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, questionIds);
        List<QuestionBankQuestion> existQuestionBankQuestion = this.list(existQueryWrapper);
        Set<Long> existQuestionIdSet = existQuestionBankQuestion.stream()
                .map(QuestionBankQuestion::getQuestionId)
                .collect(Collectors.toSet());
        questionIds = questionIds.stream()
                .filter(questionId -> !existQuestionIdSet.contains(questionId))
                .collect(Collectors.toList());
        ThrowUtils.throwIf(questionIds.isEmpty(), ErrorCode.PARAMS_ERROR, "所有题目已存在题库中");
        // 创建线程池用户执行异步任务
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                20,
                50,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        List<CompletableFuture> futures = new ArrayList<>();
        // 2.事务分批，每500条提交一次
        int batchSize = 500;
        int maxSize = questionIds.size();
        for (int i = 0; i < maxSize; i += batchSize) {
            int end = Math.min(i + batchSize, maxSize);
            log.info("sync from {} to {}", i, end);
            List<Long> subList = questionIds.subList(i, end);
            LambdaQueryWrapper<QuestionBankQuestion> wrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                    .in(QuestionBankQuestion::getQuestionId, subList);
            List<QuestionBankQuestion> questionBankQuestionList = this.list(wrapper);
            // 分批事务操作(异步)
            QuestionBankQuestionService questionBankQuestionService =
                    (QuestionBankQuestionService) AopContext.currentProxy();
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                questionBankQuestionService.batchAdd(questionBankQuestionList);
            }, threadPoolExecutor);
            futures.add(completableFuture);
        }
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        threadPoolExecutor.shutdown();
    }

    /**
     * 事务分批，避免长事务
     * @param questionBankQuestionList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchAdd(List<QuestionBankQuestion> questionBankQuestionList){
        // 提高代码健壮性，捕获可能或出现的异常转换为自定义异常
        for (QuestionBankQuestion questionBankQuestion : questionBankQuestionList) {
            Long questionId = questionBankQuestion.getQuestionId();
            Long questionBankId = questionBankQuestion.getQuestionBankId();
            try {

                boolean save = this.save(questionBankQuestion);
                ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "添加题目到题库失败");
            } catch (DataIntegrityViolationException e) {
                log.error("数据库唯一键冲突或违反其他完整性约束，题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于该题库，无法重复添加");
            } catch (DataAccessException e) {
                log.error("数据库连接问题、事务问题等导致操作失败，题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
            } catch (Exception e) {
                // 捕获其他异常，做通用处理
                log.error("添加题目到题库时发生未知错误，题目 id: {}, 题库 id: {}, 错误信息: {}",
                        questionId, questionBankId, e.getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
            }
        }
    }


    /**
     * 批量移除题目从题库
     * @param questionIdList
     * @param questionBankId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId){
        // 移除题目题库关联数据
        for (Long questionId : questionIdList) {
            LambdaQueryWrapper<QuestionBankQuestion> wrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            boolean save = this.remove(wrapper);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "移除题目到题库失败");
        }
    }
}
