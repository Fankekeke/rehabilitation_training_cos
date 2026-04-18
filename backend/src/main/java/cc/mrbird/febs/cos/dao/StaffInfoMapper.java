package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.StaffInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface StaffInfoMapper extends BaseMapper<StaffInfo> {

    /**
     * 分页获取康复师信息
     *
     * @param page          分页对象
     * @param staffInfo 康复师信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectStaffPage(Page<StaffInfo> page, @Param("staffInfo") StaffInfo staffInfo);

    /**
     * 查询康复师信息
     *
     * @param enterpriseId 康复师id
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectStaffList(@Param("enterpriseId") Integer enterpriseId);


    /**
     * 查询康复师信息
     *
     * @param enterpriseId 康复师id
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectStaffListUser(@Param("enterpriseId") Integer enterpriseId, @Param("staffId") Integer staffId);

    /**
     * 查询康复师信息
     *
     * @param ids ids
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectStaffListByUserIds(@Param("ids") List<String> ids);

}
