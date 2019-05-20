package com.mod.loan.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mod.loan.DecisionHelper;
import com.mod.loan.config.qjld.QjldConfig;
import com.mod.loan.model.dto.*;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.QjldPolicyService;
import com.mod.loan.util.ConstantUtils;
import com.mod.loan.util.RsaCodingUtil;
import com.mod.loan.util.RsaReadUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * loan-pay 2019/4/22 huijin.shuailijie Init
 */
@Slf4j
@Service
public class QjldPolicyServiceImpl implements QjldPolicyService {
    public static final String CHAR_SET = "UTF-8";
    @Autowired
    private DecisionHelper decisionHelper;
    @Autowired
    private QjldConfig qjldConfig;

    /*
     * @Description:全景雷达同步请求类
     * @Param:
     * @return:
     * @Author: huijin.shuailijie
     * @Date: 2019/4/22
     */
    @Override
    public DecisionResDetailDTO qjldPolicySync(String transId, User user, UserBank userBank) {
        DecisionBaseReqDTO decisionReqDTO = new DecisionBaseReqDTO();
        decisionReqDTO.setMember_id(qjldConfig.getQjldMemberId());
        decisionReqDTO.setTerminal_id(qjldConfig.getQjldTerminalId());
        decisionReqDTO.setRes_encrypt(Boolean.FALSE);
        decisionReqDTO.setTrans_id(transId);
        String dataContent = getDataContent(user, userBank, transId);
        decisionReqDTO.setData_content(dataContent);
        DecisionBaseResDTO decision = decisionHelper.syncDecision(decisionReqDTO);
        DecisionResDetailDTO decisionResDetailDTO = decision.getData_content();
        log.info("决策执行完成,响应信息:{}", decisionResDetailDTO);
        return decisionResDetailDTO;
    }

    /*
     * @Description:全进雷达异步请求类
     * @Param:
     * @return:
     * @Author: huijin.shuailijie
     * @Date: 2019/4/22
     */
    @Override
    public DecisionResDetailDTO qjldPolicyNoSync(String transId, User user, UserBank userBank) {
        DecisionBaseReqDTO decisionReqDTO = new DecisionBaseReqDTO();
        decisionReqDTO.setMember_id(qjldConfig.getQjldMemberId());
        decisionReqDTO.setTerminal_id(qjldConfig.getQjldTerminalId());
        decisionReqDTO.setRes_encrypt(Boolean.FALSE);
        decisionReqDTO.setTrans_id(transId);
        String dataContent = getDataContent(user, userBank, transId);
        decisionReqDTO.setData_content(dataContent);
        DecisionBaseResDTO decision = decisionHelper.nosyncDecision(decisionReqDTO);
        DecisionResDetailDTO decisionResDetailDTO = decision.getData_content();
        log.info("决策执行完成,响应信息:{}", decisionResDetailDTO);
        return decisionResDetailDTO;
    }

    @Override
    public DecisionResDetailDTO qjldPolicQuery(String transId) {
        DecisionReqDTO reqDTO = new DecisionReqDTO();
        reqDTO.setMember_id(qjldConfig.getQjldMemberId());
        reqDTO.setTrans_id(transId);
        reqDTO.setNeed_details(ConstantUtils.Y_FLAG);
        DecisionResDetailDTO decision = decisionHelper.queryDecision(reqDTO);
        log.info("决策执行完成,响应信息:{}", decision);
        return decision;
    }


    /***
     * 加密参数请求
     *
     * @return 加密数据
     */
    private String getDataContent(User user, UserBank userBank, String orderId) {

        DecisionReqDTO reqDTO = new DecisionReqDTO();
        //添加客户基本信息(姓名身份证必填)
        BaseUserDTO userDTO = new BaseUserDTO();
        userDTO.setId_name(user.getUserName());
        userDTO.setId_card(user.getUserCertNo());
        userDTO.setPhone(user.getUserPhone());
        userDTO.setBank_card_no(userBank.getCardNo());
        reqDTO.setBase_user(userDTO);
        //添加商户信息(新颜分配的商户号)
        reqDTO.setMember_id(qjldConfig.getQjldMemberId());
        //添加事件编号(在配置页面配置事件后可以使用事件编号调用到对应事件)
        reqDTO.setDecision_code(qjldConfig.getQjldType());
        //是否返回明细
        reqDTO.setNeed_details(ConstantUtils.Y_FLAG);
        //订单号(32位唯一字符串)
        reqDTO.setTrans_id(orderId);
        //异步查询回调url
        reqDTO.setNotify_url(qjldConfig.getQjldCallBackUrl());
        //添加自定义标签信息(该信息可以是map也可以是对象只要满足对应的json格式就可以)
        //没有自定义标签的可以不传这个参数
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = null;
        try {
            jsonData = objectMapper.writeValueAsString(reqDTO);
            log.info("请求JSON数据:{}", jsonData);
            jsonData = RsaCodingUtil.encryptByPrivateKey(Base64.encode(jsonData.getBytes(CHAR_SET)),
                    RsaReadUtil.getPrivateKeyFromFile(qjldConfig.getQjldKeyPath(), qjldConfig.getQjldKeyPwd()));

        } catch (Exception e) {
            log.error("转换字符串异常:{}", e);
        }

        return jsonData;
    }
}
