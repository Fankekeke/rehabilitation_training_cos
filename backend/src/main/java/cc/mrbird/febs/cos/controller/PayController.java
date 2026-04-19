package cc.mrbird.febs.cos.controller;

import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AlipayBean;
import cc.mrbird.febs.cos.entity.ServiceReserveInfo;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.IServiceReserveInfoService;
import cc.mrbird.febs.cos.service.IUserInfoService;
import cc.mrbird.febs.cos.service.PayService;
import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/cos/pay")
public class PayController {

    @Autowired
    private PayService payService;
    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IServiceReserveInfoService serviceReserveInfoService;

    /**
     * 阿里支付
     *
     * @param subject
     * @param body
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/alipay")
    public R alipay(String outTradeNo, String subject, String totalAmount, String body) throws AlipayApiException {
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(subject);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(body);
        String result = payService.aliPay(alipayBean);
        return R.ok(result);
    }

    @PostMapping(value = "/alipay")
    public R alipay(ServiceReserveInfo serviceReserveInfo) throws AlipayApiException {
        serviceReserveInfo.setCode("SOR-" + System.currentTimeMillis());
        serviceReserveInfo.setStatus("0");
        serviceReserveInfo.setCreateDate(DateUtil.formatDateTime(new Date()));

        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, serviceReserveInfo.getUserId()));
        serviceReserveInfo.setUserId(userInfo.getId());

        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(serviceReserveInfo.getCode());
        alipayBean.setSubject(serviceReserveInfo.getType());
        alipayBean.setTotal_amount(serviceReserveInfo.getTotalPrice().toString());
        alipayBean.setBody("");
        String result = payService.aliPay(alipayBean);
        return R.ok(result);
    }

    /**
     * 支付成功回调
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @GetMapping("/payBack")
    public R payBack(String orderCode) {
        ServiceReserveInfo serviceReserveInfo = serviceReserveInfoService.getOne(Wrappers.<ServiceReserveInfo>lambdaQuery().eq(ServiceReserveInfo::getCode, orderCode));
        serviceReserveInfo.setStatus("1");
        return R.ok(serviceReserveInfoService.updateById(serviceReserveInfo));
    }

//    /**
//     * 购买康复师
//     * @param subject
//     * @param body
//     * @return
//     * @throws AlipayApiException
//     */
//    @PostMapping(value = "/member")
//    public R alipayMember(String subject, String totalAmount, String body, Integer ruleId, Integer userId) throws AlipayApiException {
//        // 获取用户信息
//        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
//
//        MemberRecordInfo memberRecordInfo = new MemberRecordInfo();
//        memberRecordInfo.setStatus("0");
//        memberRecordInfo.setMemberId(ruleId);
//        memberRecordInfo.setCode("MEM-" + System.currentTimeMillis());
//        memberRecordInfo.setUserId(userInfo.getId());
//        memberRecordInfo.setPrice(new BigDecimal(totalAmount));
//        memberRecordInfoService.save(memberRecordInfo);
//
//        AlipayBean alipayBean = new AlipayBean();
//        alipayBean.setOut_trade_no(memberRecordInfo.getCode());
//        alipayBean.setSubject(subject);
//        alipayBean.setTotal_amount(totalAmount);
//        alipayBean.setBody(body);
//        String result = payService.aliPay(alipayBean);
//        return R.ok(result);
//    }

}
