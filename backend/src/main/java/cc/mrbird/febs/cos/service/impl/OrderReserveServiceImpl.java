package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.OrderReserve;
import cc.mrbird.febs.cos.dao.OrderReserveMapper;
import cc.mrbird.febs.cos.service.IOrderReserveService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class OrderReserveServiceImpl extends ServiceImpl<OrderReserveMapper, OrderReserve> implements IOrderReserveService {

    /**
     * 分页获取健身设施信息
     *
     * @param page       分页对象
     * @param orderReserve 健身设施信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryReservePage(Page<OrderReserve> page, OrderReserve orderReserve) {
        return baseMapper.queryReservePage(page, orderReserve);
    }
}
