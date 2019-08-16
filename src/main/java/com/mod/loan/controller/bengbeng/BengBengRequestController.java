package com.mod.loan.controller.bengbeng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.config.Constant;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.controller.bengbeng.handler.*;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.model.Merchant;
import com.mod.loan.model.User;
import com.mod.loan.service.MerchantService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.HttpUtils;
import com.mod.loan.util.bengbeng.BizDataUtil;
import com.mod.loan.util.bengbeng.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ author liujianjian
 * @ date 2019/5/15 18:01
 */
@Slf4j
@RestController
@RequestMapping("/bengbeng")
public class BengBengRequestController {


    @Resource
    private RedisMapper redisMapper;
    @Resource
    private UserService userService;
    @Resource
    private RongZeRequestHandler rongZeRequestHandler;
    @Resource
    private CertRequestHandler certRequestHandler;
    @Resource
    private UserInfoBaseRequestHandler userInfoBaseRequestHandler;
    @Resource
    private UserInfoAdditRequestHandler userInfoAdditRequestHandler;
    @Resource
    private AuditResultRequestHandler auditResultRequestHandler;
    @Resource
    private WithDrawRequestHandler withDrawRequestHandler;
    @Resource
    private BankRequestHandler bankRequestHandler;
    @Resource
    private RepayRequestHandler repayRequestHandler;

    @Autowired
    private MerchantService merchantService;
    @Resource
    private OrderUserMapper orderUserMapper;

    private static String logPre = "融泽入口请求, ";

    @RequestMapping("/dispatcherRequest")
    public Object dispatcherRequest(HttpServletRequest request, @RequestBody JSONObject param) {

        long s = System.currentTimeMillis();

        log.warn(logPre + "请求参数:" + param.toJSONString());

        Object result = null;
        String method = param.getString("method");
        log.info(logPre + "请求方法:" + method);

        //redis的key
        String key = null;
        try {//校验 sig

            if (StringUtils.isBlank(method)) throw new BizException(ResponseEnum.M5000);

            String sign = param.getString("sign");
            boolean check = SignUtil.checkSign(param.toJSONString(), sign);
            if (!check) throw new BizException(ResponseEnum.M4006);

            //解密 bizData
            if ("1".equals(param.getString("biz_enc"))) {
                String bizDataStr = param.getString("biz_data");
                String bizData = BizDataUtil.decryptBizData(bizDataStr, param.getString("des_key"));
                param.put("biz_data", bizData);
                log.warn(logPre + "请求方法:" + method + "解密后的数据：" + param.toJSONString());
            }

            //锁住每个请求
            //redis的key
            key = this.binRequestThread(request, param, method);
            switch (method) {
                case "fund.withdraw.req": //提交用户确认收款信息
                    result = rongZeRequestHandler.handleOrderSubmit(param);
                    break;
                case "fund.deal.contract": //查询借款合同
                    result = rongZeRequestHandler.handleQueryContract(param);
                    break;
                case "fund.order.status": //查询订单状态
                    result = rongZeRequestHandler.handleQueryOrderStatus(param);
                    break;
                case "fund.payment.req": //用户还款
                    result = rongZeRequestHandler.handleRepayment(param);
                    break;
                case "fund.bank.bind": //用户验证银行卡
                    result = bankRequestHandler.bankCardCode(param);
                    break;
                case "fund.bank.verify": //用户绑定银行卡
                    result = bankRequestHandler.bankBind(param);
                    break;
                case "fund.payment.plan": //查询还款计划
                    result = repayRequestHandler.getRepayPlan(param);
                    break;
                case "fund.payment.result": //查询还款状态
                    result = repayRequestHandler.getRepayStatus(param);
                    break;

                case "fund.cert.auth": //查询复贷黑名单信息
                    result = certRequestHandler.certAuth(param);
                    break;
                case "fund.userinfo.base": //提交用户基本信息
                    result = userInfoBaseRequestHandler.userInfoBase(param);
                    break;
                case "fund.userinfo.addit": //查询用户补充信息
                    result = userInfoAdditRequestHandler.userInfoAddit(param);
                    break;
                case "fund.audit.result": //查询审批结论
                    result = auditResultRequestHandler.auditResult(param);
                    break;
                case "fund.withdraw.trial": //试算接口
                    result = withDrawRequestHandler.withdrawTria(param);
                    break;
                // TODO: 2019/5/15 其它 method
                default:
                    throw new BizException(ResponseEnum.M5000.getCode(), "method not found");
            }
        } catch (Exception e) {
            logFail(e, "【" + method + "】方法出错：" + param.toJSONString());
            result = e instanceof BizException ? ResponseBean.fail(((BizException) e)) : ResponseBean.fail(e.getMessage());
        }
        log.info(logPre + "结束返回, result: " + JSON.toJSONString(result) + ", method: " + method + ", costTime: " + (System.currentTimeMillis() - s) + " ms");
        return result;
    }

    private String binRequestThread(HttpServletRequest request, JSONObject param, String method) throws BizException {
        RequestThread.remove();// 移除本地线程变量

        JSONObject bizData = JSONObject.parseObject(param.getString("biz_data"));

        Long uid = null;
        String orderNo = null;
        switch (method) {
            case "fund.userinfo.base": //用户基本信息
                orderNo = bizData.containsKey("orderInfo") ? bizData.getJSONObject("orderInfo").getString("order_no") : null;
                break;
            default:
                orderNo = bizData.containsKey("order_no") ? bizData.getString("order_no") : null;
        }
        log.info("订单编号:" + orderNo);
        if (StringUtils.isEmpty(orderNo)) {
            throw new BizException("订单编号不存在");
        }
        String key = redisMapper.getOrderUserKey(orderNo, UserOriginEnum.BB.getCode());
        if (redisMapper.hasKey(key)) {
            String value = redisMapper.get(key);
            if (!"null".equals(value)) {
                uid = Long.parseLong(redisMapper.get(key));
            } else {
                redisMapper.remove(key);
            }
        } else {
            uid = orderUserMapper.getUidByOrderNoAndSource(orderNo, Integer.parseInt(UserOriginEnum.BB.getCode()));
            if (uid != null) {
                redisMapper.set(key, uid);
            }
        }
        String sourceId = param.getString("source_id"); //标志用户来源的app
        String clientAlias = Constant.merchant;
        String sign = param.getString("sign");
        String token = param.getString("token");
        RequestThread.setClientAlias(clientAlias);
        RequestThread.setIp(HttpUtils.getIpAddr(request, "."));
        RequestThread.setRequestTime(System.currentTimeMillis());
        RequestThread.setToken(token);
        RequestThread.setSign(sign);
        RequestThread.setSourceId(sourceId);
        RequestThread.setUid(uid);
        //判断商户是否配置好
        Merchant merchant = merchantService.findMerchantByAlias(clientAlias);
        if (merchant == null) {
            log.info("商户【" + RequestThread.getClientAlias() + "】不存在，未配置");
            throw new BizException("商户不存在");
        }
        log.info("融泽请求方法名:" + method + ",redis的缓存key值:" + key + ",用户的id：" + uid);
        return key;
    }

    private void logFail(Exception e, String info) {
        if (e instanceof BizException)
            log.info(getPreLog() + e.getMessage() + ",相关数据：" + info);
        else
            log.error("融泽入口请求系统异常, " + getPreLog() + e.getMessage() + ",相关数据：" + info, e);
    }

    private String getPreLog() {
        Long uid = RequestThread.getUid();
        if (uid == null) return "";

        String pre = "userId: %s, username: %s, phone: %s, ", username = "", phone = "";
        User user = userService.selectByPrimaryKey(uid);
        if (user != null) {
            username = user.getUserName();
            phone = user.getUserPhone();
        }
        return String.format(pre, uid, username, phone);
    }
}
