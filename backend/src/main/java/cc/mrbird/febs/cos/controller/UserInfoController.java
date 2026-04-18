package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.IUserInfoService;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/user-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserInfoController {

    private final IUserInfoService userInfoService;

    private final UserService userService;

    @GetMapping("/detail/{userId}")
    public R userDetail(@PathVariable("userId") Integer userId) {
        return R.ok(userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId)));
    }

    /**
     * 分页查询用户信息
     *
     * @param page
     * @param userInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, UserInfo userInfo) {
        return R.ok();
    }

    /**
     * 用户详情
     * @param userId
     * @return
     */
    @GetMapping("/queryDetail")
    public R queryDetail(Integer userId) {
        return R.ok(userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId)));
    }

    @GetMapping("/list")
    public R list() {
        return R.ok(userInfoService.list());
    }

    /**
     * 新增用户信息
     *
     * @param userInfo
     * @return
     */
    @PostMapping
    public R save(UserInfo userInfo) {
        return R.ok(userInfoService.save(userInfo));
    }

    /**
     * 修改用户信息
     *
     * @param userInfo
     * @return
     */
    @PutMapping
    public R edit(UserInfo userInfo) {
        UserInfo userInfoFix = userInfoService.getById(userInfo.getId());
        userService.update(Wrappers.<User>lambdaUpdate().set(User::getName, userInfo.getName()).set(User::getImages, userInfo.getImages()).eq(User::getUserId, userInfoFix.getUserId()));
        return R.ok(userInfoService.updateById(userInfo));
    }

    /**
     * 删除用户信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(userInfoService.removeByIds(ids));
    }

}
