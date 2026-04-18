package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.OrderReserve;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IOrderReserveService extends IService<OrderReserve> {

    /**
     * 分页获取健身设施信息
     *
     * @param page       分页对象
     * @param orderReserve 健身设施信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryReservePage(Page<OrderReserve> page, OrderReserve orderReserve);
}
