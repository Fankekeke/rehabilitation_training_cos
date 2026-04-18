package cc.mrbird.febs.cos.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.service.*;
import cc.mrbird.febs.common.utils.R;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 问题回答表 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/cos/party-answers")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PartyAnswersController {

    private final IPartyAnswersService bulletinInfoService;

    private final IUserInfoService userInfoService;

    private final IStaffInfoService staffInfoService;

    private final INotifyInfoService notifyInfoService;

    private final IPartyQuestionsService partyQuestionsService;

    /**
     * 分页获取问题回答表
     *
     * @param page      分页对象
     * @param queryFrom 问题回答表
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<PartyAnswers> page, PartyAnswers queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 根据问题ID查询问题回答表
     *
     * @param questionId 问题ID
     * @return 列表
     */
    @GetMapping("/queryAnswersByQuestionId")
    public R queryAnswersByQuestionId(Integer questionId) {
        return R.ok(bulletinInfoService.queryAnswersByQuestionId(questionId));
    }

    /**
     * 查询问题回答表详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询问题回答表列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增问题回答表
     *
     * @param addFrom 问题回答表对象
     * @return 结果
     */
    @PostMapping
    public R save(PartyAnswers addFrom) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(userInfo.getId());
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));

        String message1 = "用户" + userInfo.getName() + "在" + DateUtil.formatDateTime(new Date()) + "提交了问题回答，请及时查看！";
        PartyQuestions questionInfo = partyQuestionsService.getById(addFrom.getQuestionId());
        UserInfo notifyUserInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getId, questionInfo.getUserId()));

        NotifyInfo messageInfo = new NotifyInfo(notifyUserInfo.getId(), notifyUserInfo.getCode(), message1, DateUtil.formatDateTime(new Date()));
        notifyInfoService.save(messageInfo);

        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 新增问题回答表
     *
     * @param addFrom 问题回答表对象
     * @return 结果
     */
    @PostMapping("/staffSave")
    public R staffSave(PartyAnswers addFrom) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(staffInfo.getUserId());
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改问题回答表
     *
     * @param editFrom 问题回答表对象
     * @return 结果
     */
    @PutMapping
    public R edit(PartyAnswers editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除问题回答表
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
