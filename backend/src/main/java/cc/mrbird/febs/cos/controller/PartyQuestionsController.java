package cc.mrbird.febs.cos.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cc.mrbird.febs.cos.entity.PartyQuestions;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.IPartyQuestionsService;
import cc.mrbird.febs.cos.service.IUserInfoService;
import cc.mrbird.febs.common.utils.R;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 康复师社区问题表 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/cos/party-questions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PartyQuestionsController {

    private final IPartyQuestionsService bulletinInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取康复师社区问题表
     *
     * @param page      分页对象
     * @param queryFrom 康复师社区问题表
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<PartyQuestions> page, PartyQuestions queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询所有问题
     *
     * @return 列表
     */
    @GetMapping("/queryAllQuestions")
    public R queryAllQuestions(){
        return R.ok(bulletinInfoService.queryAllQuestions());
    }

    /**
     * 查询康复师社区问题表详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询康复师社区问题表列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增康复师社区问题表
     *
     * @param addFrom 康复师社区问题表对象
     * @return 结果
     */
    @PostMapping
    public R save(PartyQuestions addFrom) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(userInfo.getId());
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        addFrom.setStatus("进行中");
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 接受回答
     *
     * @param questionId 问题ID
     * @return 结果
     */
    @GetMapping("/acceptAnswer")
    public R acceptAnswer(Integer questionId) {
        return R.ok(bulletinInfoService.update(Wrappers.<PartyQuestions>lambdaUpdate().set(PartyQuestions::getStatus, "已采纳").eq(PartyQuestions::getId, questionId)));
    }

    /**
     * 修改康复师社区问题表
     *
     * @param editFrom 康复师社区问题表对象
     * @return 结果
     */
    @PutMapping
    public R edit(PartyQuestions editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除康复师社区问题表
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
