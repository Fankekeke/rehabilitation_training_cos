package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.OrderReserve;
import cc.mrbird.febs.cos.service.IOrderReserveService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/order-reserve")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderReserveController {

    private final IOrderReserveService orderReserveService;

    /**
     * 分页获取健身设施信息
     *
     * @param page       分页对象
     * @param orderReserve 健身设施信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OrderReserve> page, OrderReserve orderReserve) {
        return R.ok(orderReserveService.queryReservePage(page, orderReserve));
    }

    /**
     * 获取健身设施信息详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(orderReserveService.getById(id));
    }

    /**
     * 新增健身设施信息
     *
     * @param orderReserve 健身设施信息
     * @return 结果
     */
    @PostMapping
    public R save(OrderReserve orderReserve) {
        orderReserve.setCode("ORE-" + System.currentTimeMillis());
        orderReserve.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(orderReserveService.save(orderReserve));
    }
    /**
     * 健身设施信息
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(orderReserveService.list());
    }

    /**
     * 修改健身设施信息
     *
     * @param orderReserve 健身设施信息
     * @return 结果
     */
    @PutMapping
    public R edit(OrderReserve orderReserve) {
        return R.ok(orderReserveService.updateById(orderReserve));
    }

    /**
     * 删除健身设施信息
     *
     * @param ids 主键
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(orderReserveService.removeByIds(ids));
    }
    
}
