package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.ServiceReserveInfoMapper;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.StaffInfoMapper;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffInfoServiceImpl extends ServiceImpl<StaffInfoMapper, StaffInfo> implements IStaffInfoService {

    private final StaffInfoMapper staffInfoService;

    private final IBulletinInfoService bulletinInfoService;

    private final IUserInfoService userInfoService;

    private final ServiceReserveInfoMapper serviceReserveInfoService;

    private final IOrderReserveService orderReserveService;

    private final IDeviceInfoService deviceInfoService;

    private final IAttendanceInfoService attendanceInfoService;

    /**
     * 分页获取康复师信息
     *
     * @param page          分页对象
     * @param staffInfo 康复师信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectStaffPage(Page<StaffInfo> page, StaffInfo staffInfo) {
        return baseMapper.selectStaffPage(page, staffInfo);
    }

    /**
     * 获取康复师列表
     *
     * @param userId 康复师ID
     * @return 列表
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryStaffListRecommend(Integer userId) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        if (userInfo == null) {
            return Collections.emptyList();
        }

        List<ServiceReserveInfo> orderList = serviceReserveInfoService.selectList(Wrappers.<ServiceReserveInfo>lambdaQuery().eq(ServiceReserveInfo::getUserId, userInfo.getId()));

        List<StaffInfo> staffInfoList = this.list();
        List<OrderReserve> orderReserveList = orderReserveService.list(Wrappers.<OrderReserve>lambdaQuery().eq(OrderReserve::getStatus, 1));
        Map<Integer, List<OrderReserve>> orderReserveMap = orderReserveList.stream().collect(Collectors.groupingBy(OrderReserve::getUserId));

        // 获取所有用户的预约记录，用于协同过滤
        List<ServiceReserveInfo> allServiceReserves = serviceReserveInfoService.selectList(Wrappers.<ServiceReserveInfo>lambdaQuery());

        // 构建用户-康复师评分矩阵
        Map<Integer, Map<Integer, Double>> userStaffMatrix = new java.util.HashMap<>();
        for (ServiceReserveInfo reserve : allServiceReserves) {
            if (reserve.getUserId() != null && reserve.getWorkUserId() != null) {
                userStaffMatrix.computeIfAbsent(reserve.getUserId(), k -> new java.util.HashMap<>());
                Map<Integer, Double> staffScores = userStaffMatrix.get(reserve.getUserId());
                staffScores.merge(reserve.getWorkUserId(), 1.0, Double::sum);
            }
        }

        // 获取当前用户已预约的康复师
        Set<Integer> currentUserStaffs = orderList.stream()
                .map(ServiceReserveInfo::getWorkUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 计算用户相似度（基于Jaccard相似系数）
        Map<Integer, Double> userSimilarity = new java.util.HashMap<>();
        Set<Integer> targetUserStaffs = userStaffMatrix.getOrDefault(userInfo.getId(), Collections.emptyMap()).keySet();

        for (Map.Entry<Integer, Map<Integer, Double>> entry : userStaffMatrix.entrySet()) {
            Integer otherUserId = entry.getKey();
            if (otherUserId.equals(userInfo.getId())) {
                continue;
            }

            Set<Integer> otherUserStaffs = entry.getValue().keySet();

            // 计算交集
            Set<Integer> intersection = new java.util.HashSet<>(targetUserStaffs);
            intersection.retainAll(otherUserStaffs);

            // 计算并集
            Set<Integer> union = new java.util.HashSet<>(targetUserStaffs);
            union.addAll(otherUserStaffs);

            // Jaccard相似度
            if (!union.isEmpty()) {
                double similarity = (double) intersection.size() / union.size();
                if (similarity > 0) {
                    userSimilarity.put(otherUserId, similarity);
                }
            }
        }

        // 基于相似用户推荐康复师
        Map<Integer, Double> staffRecommendScore = new java.util.HashMap<>();
        for (Map.Entry<Integer, Double> simEntry : userSimilarity.entrySet()) {
            Integer similarUserId = simEntry.getKey();
            Double similarity = simEntry.getValue();

            Map<Integer, Double> similarUserStaffs = userStaffMatrix.get(similarUserId);
            if (similarUserStaffs != null) {
                for (Map.Entry<Integer, Double> staffEntry : similarUserStaffs.entrySet()) {
                    Integer staffId = staffEntry.getKey();
                    // 只推荐当前用户未预约过的康复师
                    if (!currentUserStaffs.contains(staffId)) {
                        staffRecommendScore.merge(staffId, similarity * staffEntry.getValue(), Double::sum);
                    }
                }
            }
        }

        // 按推荐分数排序
        List<Map.Entry<Integer, Double>> sortedStaffs = staffRecommendScore.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        // 构建返回结果
        List<LinkedHashMap<String, Object>> result = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sortedStaffs) {
            Integer staffId = entry.getKey();
            StaffInfo staff = staffInfoList.stream()
                    .filter(s -> s.getId().equals(staffId))
                    .findFirst()
                    .orElse(null);

            if (staff != null) {
                LinkedHashMap<String, Object> staffMap = new LinkedHashMap<>();
                staffMap.put("staff", staff);
                staffMap.put("recommendScore", entry.getValue());

                // 设置该康复师的订单预约信息
                List<OrderReserve> staffOrders = orderReserveMap.get(staffId);

                staffMap.put("orderReserveList", staffOrders);
                staffMap.put("orderCount", CollectionUtil.isEmpty(staffOrders) ? 0 : staffOrders.size());

                result.add(staffMap);
            }
        }

        // 如果推荐列表为空，返回所有康复师（按订单数排序）
        if (result.isEmpty()) {
            for (StaffInfo staff : staffInfoList) {
                LinkedHashMap<String, Object> staffMap = new LinkedHashMap<>();
                staffMap.put("staff", staff);
                staffMap.put("recommendScore", 0.0);

                // 设置该康复师的订单预约信息
                List<OrderReserve> staffOrders = orderReserveMap.get(staff.getId());

                staffMap.put("orderReserveList", staffOrders);
                staffMap.put("orderCount", CollectionUtil.isEmpty(staffOrders) ? 0 : staffOrders.size());

                result.add(staffMap);
            }

            // 按订单数排序
            result.sort((a, b) -> {
                Integer countA = (Integer) a.get("orderCount");
                Integer countB = (Integer) b.get("orderCount");
                return countB.compareTo(countA);
            });
        }

        return result;
    }

    /**
     * 查询用户信息详情【公告信息】
     *
     * @param userId 主键ID
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectBulletinDetail(Integer userId) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("user", null);
                put("bulletin", Collections.emptyList());
            }
        };
        StaffInfo userInfo = this.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, userId));
        if (userInfo == null) {
            return result;
        }
        result.put("user", userInfo);

        // 公告信息
        List<BulletinInfo> bulletinInfoList = bulletinInfoService.list();
        result.put("bulletin", bulletinInfoList);
        return result;
    }

    /**
     * 查询康复师信息
     *
     * @param enterpriseId 康复师id
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectStaffList(Integer enterpriseId) {
        return baseMapper.selectStaffList(enterpriseId);
    }

    /**
     * 获取康复师列表
     *
     * @param enterpriseId 康复师ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryStaffListByStaff(Integer enterpriseId, Integer userId) {
        return baseMapper.selectStaffListUser(enterpriseId, userId);
    }

    /**
     * 根据用户id查询康复师信息
     *
     * @param userId 用户id
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryStaffByUserId(Integer userId) {
        // 获取康复师信息
        StaffInfo staffInfo = staffInfoService.selectOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, userId));
//        if (staffInfo.getMemberLevel() != null) {
//            LevelRuleInfo levelRuleInfo = levelRuleInfoService.getById(staffInfo.getMemberLevel());
//            staffInfo.setLevelName(levelRuleInfo.getLevelName());
//        }
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("staff", staffInfo);
            }
        };
        return result;
    }

    /**
     * 查询康复师信息
     *
     * @param ids ids
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectStaffListByUserIds(List<String> ids) {
        return baseMapper.selectStaffListByUserIds(ids);
    }

    /**
     * 获取首页数据
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryHomeData() {
        return null;
    }
}
