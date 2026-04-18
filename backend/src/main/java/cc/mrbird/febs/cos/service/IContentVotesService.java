package cc.mrbird.febs.cos.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cc.mrbird.febs.cos.entity.ContentVotes;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IContentVotesService extends IService<ContentVotes> {

    /**
     * 分页获取顶踩投票记录表
     *
     * @param page      分页对象
     * @param queryFrom 顶踩投票记录表
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<ContentVotes> page, ContentVotes queryFrom);
}
