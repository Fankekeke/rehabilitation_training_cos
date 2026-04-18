package cc.mrbird.febs.cos.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.service.*;
import cc.mrbird.febs.common.utils.R;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 顶踩投票记录表 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/cos/content-votes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContentVotesController {

    private final IContentVotesService bulletinInfoService;

    private final IPartyAnswersService partyAnswersService;

    private final IUserInfoService userInfoService;

    private final INotifyInfoService notifyInfoService;

    private final IPartyQuestionsService partyQuestionsService;

    /**
     * 分页获取顶踩投票记录表
     *
     * @param page      分页对象
     * @param queryFrom 顶踩投票记录表
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ContentVotes> page, ContentVotes queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询顶踩投票记录表详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询顶踩投票记录表列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 根据用户ID和问题ID查询顶踩投票记录表
     *
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 列表
     */
    @GetMapping("/queryVotesByUser")
    public R queryVotesByUser(Integer userId, Integer questionId) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        List<PartyAnswers> partyAnswers = partyAnswersService.list(Wrappers.<PartyAnswers>lambdaQuery().eq(PartyAnswers::getQuestionId, questionId));
        if (CollectionUtil.isEmpty(partyAnswers)) {
            return R.ok(Collections.emptyList());
        }

        List<Integer> answerIds = partyAnswers.stream().map(PartyAnswers::getId).collect(Collectors.toList());
        return R.ok(bulletinInfoService.list(Wrappers.<ContentVotes>lambdaQuery().eq(ContentVotes::getUserId, userInfo.getId()).in(ContentVotes::getAnswerId, answerIds)));
    }

    /**
     * 新增顶踩投票记录表
     *
     * @param addFrom 顶踩投票记录表对象
     * @return 结果
     */
    @PostMapping
    public R save(ContentVotes addFrom) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(userInfo.getId());
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));

        bulletinInfoService.remove(Wrappers.<ContentVotes>lambdaQuery().eq(ContentVotes::getUserId, addFrom.getUserId()).eq(ContentVotes::getAnswerId, addFrom.getAnswerId()));

        PartyAnswers partyAnswers = partyAnswersService.getById(addFrom.getAnswerId());
        UserInfo notifyUserInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getId, partyAnswers.getUserId()));

        String message1 = "用户" + userInfo.getName() + "对您的回答进行了" + (addFrom.getVoteType() == 1 ? "顶" : "踩") + "操作";
        NotifyInfo messageInfo = new NotifyInfo(Math.toIntExact(notifyUserInfo.getId()), notifyUserInfo.getCode(), message1, DateUtil.formatDateTime(new Date()));
        notifyInfoService.save(messageInfo);

        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改顶踩投票记录表
     *
     * @param editFrom 顶踩投票记录表对象
     * @return 结果
     */
    @PutMapping
    public R edit(ContentVotes editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除顶踩投票记录表
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
