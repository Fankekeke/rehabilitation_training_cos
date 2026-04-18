package cc.mrbird.febs.cos.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cc.mrbird.febs.cos.entity.PartyQuestions;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IPartyQuestionsService extends IService<PartyQuestions> {

    /**
     * 分页获取康复师社区问题表
     *
     * @param page      分页对象
     * @param queryFrom 康复师社区问题表
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<PartyQuestions> page, PartyQuestions queryFrom);

    /**
     * 查询所有问题
     *
     * @return 列表
     */
    List<LinkedHashMap<String, Object>> queryAllQuestions();
}
