package com.mod.loan.controller.contract;

import com.mod.loan.config.Constant;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 合同相关
 *
 * @ author liujianjian
 * @ date 2019/5/19 14:08
 */
@RestController
@RequestMapping("/contract")
public class ContractController {

    @Resource
    private UserService userService;
    @Resource
    private UserBankService userBankService;
    @Resource
    private OrderMapper orderMapper;

    /**
     * 查询借款合同需要的信息
     *
     * @param uid    用户id
     * @param source 订单来源
     * @return
     */
    @RequestMapping("/queryLoanInfo")
    public Object queryLoanInfo(long uid, int source) {
        String JF = Constant.companyName; //甲方
        String YF = ""; //乙方
        String idCardNo = ""; //乙方身份证号
        String bankCardNo = ""; //乙方收款/扣款银行卡号
        String bankName = ""; //乙方银行名称
//        String bankAddress = ""; //开户地址
        String loanAmount = "1500.00"; //贷款金额，元
        String signTime = ""; //协议签署时间
        String tel = ""; //乙方手机号
        String loanNo = ""; //贷款协议号

        Map<String, Object> map = new HashMap<>();

        User user = userService.selectByPrimaryKey(uid);
        if (user != null) {
            YF = user.getUserName();
            idCardNo = user.getUserCertNo();
            tel = user.getUserPhone();

            UserBank userBank = userBankService.selectUserCurrentBankCard(uid);
            if (userBank != null) {
                bankCardNo = userBank.getCardNo();
                bankName = userBank.getCardName();
            }

            Order order = orderMapper.findUserFirstOrder(uid, source);
            if (order != null) {
                if (order.getCreateTime() != null) {
                    signTime = DateFormatUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                    loanNo = DateFormatUtils.format(order.getCreateTime(), "yyyyMMddHHmmssSSS") + uid;
                }
                loanAmount = order.getBorrowMoney().toString();
            }
        }
        map.put("JF", JF);
        map.put("YF", YF);
        map.put("idCardNo", idCardNo);
        map.put("bankCardNo", bankCardNo);
        map.put("bankName", bankName);
//        map.put("bankAddress", bankAddress);
        map.put("loanAmount", loanAmount);
        map.put("signTime", signTime);
        map.put("tel", tel);
        map.put("loanNo", loanNo);

        return map;
    }
}