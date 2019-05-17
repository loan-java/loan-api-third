package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.JuHeCallBackEnum;
import com.mod.loan.common.enums.MerchantEnum;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.message.OrderRepayQueryMessage;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderRepay;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.*;
import com.mod.loan.util.StringUtil;
import com.mod.loan.util.TimeUtils;
import com.mod.loan.util.kuaiqian.Post;
import com.mod.loan.util.kuaiqian.entity.TransInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

/**
 * @author kk
 */
@Service
public class KuaiQianServiceImpl implements KuaiQianService {
    private static Logger logger = LoggerFactory.getLogger(KuaiQianServiceImpl.class);

    @Autowired
    private UserBankService userBankService;
    @Resource
    private OrderMapper orderMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private OrderRepayService orderRepayService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CallBackJuHeService callBackJuHeService;

    /**
     * 预绑卡
     *
     * @param uid       用户id
     * @param cardNo    银行卡号
     * @param cardPhone 手机号
     * @return ResultMessage
     */
    @Override
    public ResultMessage sendKuaiQianSms(Long uid, String cardNo, String cardPhone) {
        User user = userService.selectByPrimaryKey(uid);

        TransInfo transInfo = new TransInfo();
        //版本号
        String version = Constant.kuaiQianVersion;
        //商户编号
        String merchantId = Constant.kuaiQianMemberId;
        //终端编号
        String terminalId = Constant.kuaiQianTerminalId;
        //客户号
        String customerId = Constant.KUAI_QIAN_UID_PFX + uid;
        //外部跟踪编号
        String externalRefNumber = StringUtil.getOrderNumber("ps");
        //持卡人姓名
        String cardHolderName = user.getUserName();
        //证件类型
        String idType = "0";
        //证件号
        String cardHolderId = user.getUserCertNo();

        //设置手机动态鉴权节点
        transInfo.setRecordeText_1("indAuthContent");
        transInfo.setRecordeText_2("ErrorMsgContent");

        //Tr1报文拼接
        StringBuilder str1Xml = new StringBuilder();

        str1Xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        str1Xml.append("<MasMessage xmlns=\"http://www.99bill.com/mas_cnp_merchant_interface\">");
        str1Xml.append("<version>").append(version).append("</version>");
        str1Xml.append("<indAuthContent>");
        str1Xml.append("<merchantId>").append(merchantId).append("</merchantId>");
        str1Xml.append("<terminalId>").append(terminalId).append("</terminalId>");
        str1Xml.append("<customerId>").append(customerId).append("</customerId>");
        str1Xml.append("<externalRefNumber>").append(externalRefNumber).append("</externalRefNumber>");
        str1Xml.append("<pan>").append(cardNo).append("</pan>");
        str1Xml.append("<cardHolderName>").append(cardHolderName).append("</cardHolderName>");
        str1Xml.append("<idType>").append(idType).append("</idType>");
        str1Xml.append("<cardHolderId>").append(cardHolderId).append("</cardHolderId>");
        str1Xml.append("<phoneNO>").append(cardPhone).append("</phoneNO>");
        str1Xml.append(" <bindType>0</bindType>");
        str1Xml.append("</indAuthContent>");
        str1Xml.append("</MasMessage>");

        System.out.println("tr1报文  str1Xml = " + str1Xml.toString());

        //TR2接收的数据
        HashMap respXml;
        try {
            respXml = Post.sendPost(Constant.kuaiQianSendSmsUrl, str1Xml.toString(), transInfo);
        } catch (Exception e) {
            logger.error("快钱签约申请接口请求异常，param={}， e", str1Xml.toString(), e);
            return new ResultMessage(ResponseEnum.M4000);
        }
        logger.info("快钱签约申请接口返回参数，respXml = {}", respXml);

        if (respXml == null) {
            logger.error("快钱签约申请接口请求异常，读取内容出错");
            return new ResultMessage(ResponseEnum.M4000);
        } else {
            //如果TR2获取的应答码responseCode的值为00时，成功
            if ("00".equals(respXml.get("responseCode"))) {
                /* 进行数据库的逻辑操作，比如更新数据库或插入记录。 */
                System.out.println("卡信息验证交易成功");

                JSONObject object = new JSONObject();
                object.put("externalRefNumber", externalRefNumber);
                object.put("token", respXml.get("token"));
                redisMapper.set(RedisConst.user_bank_bind + uid, object.toJSONString(), 600);
                return new ResultMessage(ResponseEnum.M2000);
            }
        }
        logger.error("快钱签约短信发送失败，请求参数为={},响应参数为={}", str1Xml.toString(), JSON.toJSONString(respXml));
        return new ResultMessage(ResponseEnum.M4000.getCode(), respXml.get("responseTextMessage") == null ?
                respXml.get("errorMessage") == null ? "银行卡签约短信发送失败" : respXml.get("errorMessage").toString() :
                respXml.get("responseTextMessage").toString());
    }

    /**
     * 签约绑卡
     *
     * @param validateCode 验证码
     * @param uid          用户id
     * @param bindInfo     redis信息
     * @param cardNo       银行卡号
     * @param cardPhone    手机号
     * @param bankCode     银行卡编码
     * @param bankName     银行名称
     * @return ResultMessage
     */
    @Override
    public ResultMessage bindKuaiQianSms(String validateCode, Long uid, String bindInfo, String cardNo, String cardPhone, String bankCode, String bankName) {
        TransInfo transInfo = new TransInfo();

        //版本号
        String version = Constant.kuaiQianVersion;
        //商户编号
        String merchantId = Constant.kuaiQianMemberId;
        //终端编号
        String terminalId = Constant.kuaiQianTerminalId;
        //客户号
        String customerId = Constant.KUAI_QIAN_UID_PFX + uid.toString();

        JSONObject object = JSON.parseObject(bindInfo);
        //外部跟踪编号
        String externalRefNumber = object.getString("externalRefNumber");
        //安全校验值
        String token = object.getString("token");

        //设置手机动态鉴权节点
        transInfo.setRecordeText_1("indAuthDynVerifyContent");
        transInfo.setRecordeText_2("ErrorMsgContent");

        //Tr1报文拼接
        StringBuilder str1Xml = new StringBuilder();

        str1Xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        str1Xml.append("<MasMessage xmlns=\"http://www.99bill.com/mas_cnp_merchant_interface\">");
        str1Xml.append("<version>").append(version).append("</version>");
        str1Xml.append("<indAuthDynVerifyContent>");
        str1Xml.append("<merchantId>").append(merchantId).append("</merchantId>");
        str1Xml.append("<terminalId>").append(terminalId).append("</terminalId>");
        str1Xml.append("<customerId>").append(customerId).append("</customerId>");
        str1Xml.append("<externalRefNumber>").append(externalRefNumber).append("</externalRefNumber>");
        str1Xml.append("<pan>").append(cardNo).append("</pan>");
        str1Xml.append("<validCode>").append(validateCode).append("</validCode>");
        str1Xml.append("<token>").append(token).append("</token>");
        str1Xml.append("<phoneNO>").append(cardPhone).append("</phoneNO>");
        str1Xml.append("</indAuthDynVerifyContent>");
        str1Xml.append("</MasMessage>");

        String url = Constant.kuaiQianBindSmsUrl;
        //https://mas.99bill.com/cnp/ind_auth_verify    //生产环境地址

        //TR2接收的数据
        HashMap respXml;
        try {
            respXml = Post.sendPost(url, str1Xml.toString(), transInfo);
        } catch (Exception e) {
            logger.error("快钱签约短信验证接口请求异常，param={}， e", str1Xml.toString(), e);
            return new ResultMessage(ResponseEnum.M4000);
        }
        logger.info("快钱签约短信验证接口返回参数，respXml = {}", respXml);

        if (respXml == null) {
            logger.error("快钱签约短信验证接口，读取内容出错");
            return new ResultMessage(ResponseEnum.M4000);
        } else {
            //如果TR2获取的应答码responseCode的值为00时，成功
            if ("00".equals(respXml.get("responseCode"))) {
                /* 进行数据库的逻辑操作，比如更新数据库或插入记录。 */
                System.out.println("卡信息验证交易成功");

                UserBank userBank = new UserBank();
                userBank.setCardCode(bankCode);
                userBank.setCardName(bankName);
                userBank.setCardNo(cardNo);
                userBank.setCardPhone(cardPhone);
                userBank.setCardStatus(1);
                userBank.setCreateTime(new Date());
                userBank.setForeignId(respXml.get("payToken").toString());
                userBank.setUid(uid);
                userBank.setBindType(MerchantEnum.kuaiqian.getCode());
                userService.insertUserBank(uid, userBank);
                redisMapper.remove(RedisConst.user_bank_bind + uid);
                return new ResultMessage(ResponseEnum.M2000, userBank.getId());
            }
        }
        logger.error("快钱签约短信验证失败，请求参数为={},响应参数为={}", str1Xml.toString(), JSON.toJSONString(respXml));
        return new ResultMessage(ResponseEnum.M4000.getCode(), respXml.get("responseTextMessage") == null ?
                respXml.get("errorMessage") == null ? "银行卡签约短信验证失败" : respXml.get("errorMessage").toString() :
                respXml.get("responseTextMessage").toString());
    }

    /**
     * 还款
     *
     * @param orderNo 订单号
     * @return ResultMessage
     */
    @Override
    public ResultMessage repay(String orderNo) {
        Long uid = RequestThread.getUid();
        Order order = orderMapper.findByOrderNoAndUid(orderNo, uid);
        return repay(order);
    }

    @Override
    public ResultMessage repay(Order order) {

        String orderNo = order.getOrderNo();

        long uid = order.getUid();

        UserBank userBank = userBankService.selectUserCurrentBankCard(uid);

        TransInfo transInfo = new TransInfo();
        /* 消费信息 */
        //版本号
        String version = Constant.kuaiQianVersion;
        //交易类型
        String txnType = "PUR";
        //特殊交易标志
        String spFlag = "QPay02";
        //消息状态
        String interactiveStatus = "TR1";
        //支付标记payToken
        String payToken = userBank.getForeignId();
        //交易金额,以元为单位，小数点后最多两位
        String amount = order.getShouldRepay().toPlainString();
        //String amount = "1";
        //商户编号
        String merchantId = Constant.kuaiQianMemberId;
        //终端编号
        String terminalId = Constant.kuaiQianTerminalId;
        //商户端交易时间
        String entryTime = TimeUtils.parseTime(new Date(), TimeUtils.dateformat5);
        //外部跟踪号
        String externalRefNumber = StringUtil.getOrderNumber("rp");
        //客户号
        String customerId = Constant.KUAI_QIAN_UID_PFX + uid;
        //tr3回调地址
        String tr3Url = "http://pay.zzjb.xin/notify/cnpPayNotify";

        //设置消费交易的两个节点
        transInfo.setRecordeText_1("TxnMsgContent");
        transInfo.setRecordeText_2("ErrorMsgContent");

        //Tr1报文拼接
        StringBuilder str1Xml = new StringBuilder();

        str1Xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        str1Xml.append("<MasMessage xmlns=\"http://www.99bill.com/mas_cnp_merchant_interface\">");
        str1Xml.append("<version>").append(version).append("</version>");

        str1Xml.append("<TxnMsgContent>");
        str1Xml.append("<interactiveStatus>").append(interactiveStatus).append("</interactiveStatus>");
        str1Xml.append("<spFlag>").append(spFlag).append("</spFlag>");
        str1Xml.append("<txnType>").append(txnType).append("</txnType>");
        str1Xml.append("<merchantId>").append(merchantId).append("</merchantId>");
        str1Xml.append("<terminalId>").append(terminalId).append("</terminalId>");
        str1Xml.append("<externalRefNumber>").append(externalRefNumber).append("</externalRefNumber>");
        str1Xml.append("<entryTime>").append(entryTime).append("</entryTime>");
        str1Xml.append("<amount>").append(amount).append("</amount>");
        str1Xml.append("<customerId>").append(customerId).append("</customerId>");
        str1Xml.append("<payToken>").append(payToken).append("</payToken>");
        str1Xml.append("<tr3Url>").append(tr3Url).append("</tr3Url>");

        str1Xml.append("<extMap>");
        str1Xml.append("<extDate><key>phone</key><value></value></extDate>");
        str1Xml.append("<extDate><key>validCode</key><value></value></extDate>");
        str1Xml.append("<extDate><key>savePciFlag</key><value>0</value></extDate>");
        str1Xml.append("<extDate><key>token</key><value></value></extDate>");
        str1Xml.append("<extDate><key>payBatch</key><value>2</value></extDate>");
        str1Xml.append("</extMap>");

        str1Xml.append("</TxnMsgContent>");
        str1Xml.append("</MasMessage>");

        //https://mas.99bill.com/cnp/purchase     //生产环境地址

        //TR2接收的数据
        HashMap respXml;
        try {
            respXml = Post.sendPost(Constant.kuaiQianRepayUrl, str1Xml.toString(), transInfo);
        } catch (Exception e) {
            logger.error("快钱协议支付接口请求异常，param={}， e", str1Xml.toString(), e);
            return new ResultMessage(ResponseEnum.M4000);
        }
        logger.info("快钱协议支付接口返回参数，respXml = {}", respXml);

        if (respXml == null) {
            logger.error("快钱协议支付接口，读取内容出错，请求参数={}", str1Xml.toString());
            return new ResultMessage(ResponseEnum.M4000);
        } else {
            //如果TR2获取的应答码responseCode的值为00时，成功
            if ("00".equals(respXml.get("responseCode"))
                    || "C0".equals(respXml.get("responseCode"))
                    || "68".equals(respXml.get("responseCode"))) {
                /* 进行数据库的逻辑操作，比如更新数据库或插入记录。 */
                logger.info("快钱协议支付接口, 调用成功, 交易成功, orderNo={}, repayNo={}", orderNo, externalRefNumber);
                OrderRepay orderRepay = new OrderRepay();
                orderRepay.setRepayNo(externalRefNumber);
                orderRepay.setUid(uid);
                orderRepay.setOrderId(order.getId());
                orderRepay.setRepayType(1);
                BigDecimal repayMoney = new BigDecimal(amount);
                orderRepay.setRepayMoney(repayMoney);
                orderRepay.setBank(userBank.getCardName());
                orderRepay.setBankNo(userBank.getCardNo());
                orderRepay.setCreateTime(new Date());
                orderRepay.setUpdateTime(new Date());
                orderRepay.setRepayStatus(0);
                orderRepayService.insertSelective(orderRepay);

                OrderRepayQueryMessage message = new OrderRepayQueryMessage();
                message.setMerchantAlias(RequestThread.getClientAlias());
                message.setRepayNo(externalRefNumber);
                message.setTimes(1);
                message.setRepayType(1);
                rabbitTemplate.convertAndSend(RabbitConst.kuaiqian_queue_repay_order_query, message);

                JSONObject object = new JSONObject();
                object.put("repayOrderNo", externalRefNumber);
                object.put("shouldRepayAmount", order.getShouldRepay().toPlainString());
                return new ResultMessage(ResponseEnum.M2000, object);
            }
        }

        User user = userService.selectByPrimaryKey(uid);
        if (respXml.get("responseTextMessage") != null) {
            callBackJuHeService.callBack(user, orderNo, JuHeCallBackEnum.REPAY_FAILED,
                    respXml.get("responseTextMessage").toString());
        } else {
            callBackJuHeService.callBack(user, orderNo, JuHeCallBackEnum.REPAY_FAILED);
        }

        logger.error("快钱还款失败，请求参数为={},响应参数为={}", str1Xml.toString(), JSON.toJSONString(respXml));
        return new ResultMessage(ResponseEnum.M4000.getCode(), respXml.get("responseTextMessage") == null ?
                respXml.get("errorMessage") == null ? "还款失败" : respXml.get("errorMessage").toString() :
                respXml.get("responseTextMessage").toString());
    }
}
