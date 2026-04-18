package cc.mrbird.febs.cos.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cc.mrbird.febs.cos.entity.PartyAnswers;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface PartyAnswersMapper extends BaseMapper<PartyAnswers> {

    /**
     * 分页获取问题回答表
     *
     * @param page      分页对象
     * @param queryFrom 问题回答表
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<PartyAnswers> page, @Param("queryFrom") PartyAnswers queryFrom);

    /**
     * 根据问题ID查询问题回答表
     *
     * @param questionId 问题ID
     * @return 列表
     */
    List<LinkedHashMap<String, Object>> queryAnswersByQuestionId(@Param("questionId") Integer questionId);
}
