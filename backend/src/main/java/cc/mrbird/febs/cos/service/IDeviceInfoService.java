package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.DeviceInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com
 */
public interface IDeviceInfoService extends IService<DeviceInfo> {

    /**
     * 分页获取健身设施信息
     *
     * @param page       分页对象
     * @param deviceInfo 健身设施信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryDeviceList(Page<DeviceInfo> page, DeviceInfo deviceInfo);
}
