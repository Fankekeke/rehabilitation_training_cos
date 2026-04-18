package cc.mrbird.febs.cos.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cc.mrbird.febs.cos.dao.PartyAnswersMapper;
import cc.mrbird.febs.cos.entity.PartyAnswers;
import cc.mrbird.febs.cos.service.IPartyAnswersService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class PartyAnswersServiceImpl extends ServiceImpl<PartyAnswersMapper, PartyAnswers> implements IPartyAnswersService {

    /**
     * 分页获取问题回答表
     *
     * @param page      分页对象
     * @param queryFrom 问题回答表
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<PartyAnswers> page, PartyAnswers queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }

    /**
     * 根据问题ID查询问题回答表
     *
     * @param questionId 问题ID
     * @return 列表
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryAnswersByQuestionId(Integer questionId) {
        return baseMapper.queryAnswersByQuestionId(questionId);
    }
}
