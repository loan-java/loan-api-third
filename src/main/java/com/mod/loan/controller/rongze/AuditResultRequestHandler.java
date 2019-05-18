package com.mod.loan.controller.rongze;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.ResponseBean;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.Blacklist;
import com.mod.loan.model.Order;
import com.mod.loan.model.User;
import com.mod.loan.service.BlacklistService;
import com.mod.loan.service.OrderService;
import com.mod.loan.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询审批结论
 */
@Slf4j
@Component
public class AuditResultRequestHandler {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BlacklistService blacklistService;


    //查询审批结论
    ResponseBean<Map<String, Object>> auditResult(JSONObject param) throws BizException {
        Map<String, Object> map = new HashMap<>();
        String message="成功";
        JSONObject bizData = param.getJSONObject("biz_data");
        log.info("===============查询审批结论开始====================" + bizData.toJSONString());



        log.info("===============查询审批结论结束====================");
        return  ResponseBean.success(map);
    }


}
