package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mod.loan.config.Constant;
import com.mod.loan.mapper.UserBankMapper;
import com.mod.loan.model.User;
import com.mod.loan.model.UserBank;
import com.mod.loan.service.*;
import com.mod.loan.util.baofoo.rsa.RsaCodingUtil;
import com.mod.loan.util.baofoo.util.SecurityUtil;
import com.mod.loan.util.typeA.HttpUtils;
import com.mod.loan.util.typeA.MD5Utils;
import com.mod.loan.util.typeA.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ author liujianjian
 * @ date 2019/5/17 15:54
 */
@Slf4j
@Service
public class TypeAServiceImpl implements TypeAService {

    /**
     * 获取是否是黑名单
     */
    @Override
    public boolean getInfoByTypeA(User user, String orderNo) {
        try {
            /** 1、 商户号 **/
            String member_id = Constant.typeaMemberId;
            /** 2、终端号 **/
            String terminal_id = Constant.typeaTerminalId;
            /** 3、请求地址 **/
            String url;
            Map<String, String> headers = new HashMap<>();
            String postString = null;

            String id_no = user.getUserCertNo();
            String id_name = user.getUserName();
            String phone_no = user.getUserPhone();
            String bankcard_no = "";
            String versions = Constant.typeaVersions;
            url = Constant.typeaUrl;
            log.info("指针A原始数据:id_no:" + id_no + ",id_name:" + id_name + ",phone_no:" + phone_no + ",bankcard_no:" + bankcard_no);

            id_no = MD5Utils.encryptMD5(id_no.trim());
            id_name = MD5Utils.encryptMD5(id_name.trim());
            bankcard_no = MD5Utils.encryptMD5(bankcard_no.trim());
            phone_no = MD5Utils.encryptMD5(phone_no.trim());

            log.info("指针A32位小写MD5加密后数据:id_no:" + id_no + ",id_name:" + id_name + ",phone_no:" + phone_no + ",bankcard_no:"
                    + bankcard_no);

            String trade_date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 订单日期
            String trans_id = orderNo;// 必须入库 并且唯一 商户订单号

            String XmlOrJson = "";
            /** 组装参数 **/
            Map<Object, Object> ArrayData = new HashMap<Object, Object>();
            ArrayData.put("member_id", member_id);
            ArrayData.put("terminal_id", terminal_id);
            ArrayData.put("trade_date", trade_date);
            ArrayData.put("trans_id", trans_id);
            ArrayData.put("phone_no", phone_no);
            ArrayData.put("bankcard_no", bankcard_no);
            ArrayData.put("versions", versions);
            ArrayData.put("encrypt_type", "MD5");// MD5：标准32位小写(推荐) SHA256：标准64位

            ArrayData.put("id_no", id_no);
            ArrayData.put("id_name", id_name);

            JSONObject jsonObjectFromMap = JSONObject.parseObject(JSONObject.toJSONString(ArrayData));
            XmlOrJson = jsonObjectFromMap.toString();
            log.info("指针A====请求明文:" + XmlOrJson);

            /** base64 编码 **/
            String base64str = SecurityUtil.Base64Encode(XmlOrJson);
            base64str = base64str.replaceAll("\r\n", "");//重要 避免出现换行空格符
            log.info("指针Abase64str:" + base64str);
            /** rsa加密 **/
//            String pfxpath = Constant.typeaPfxName;// 商户私钥 todo 正式/测试
            String pfxpath = "E://other_project/key-typea/8000013189_pri.pfx";// 商户私钥 todo 本地

            String pfxpwd = Constant.typeaPfxPwd;// 私钥密码

            String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str, pfxpath, pfxpwd);// 加密数据
            log.info("指针A====加密串:" + data_content);

            log.info("指针Aurl:" + url);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("member_id", member_id);
            params.put("terminal_id", terminal_id);
            params.put("data_type", "json");
            params.put("data_content", data_content);

            postString = HttpUtils.doPostByForm(url, headers, params);
            log.info("指针A请求返回：" + postString);

            /** ================处理返回结果============= **/
            if (postString.isEmpty()) {// 判断参数是否为空
                log.error("指针A=====返回数据为空" + postString);
                return false;
            }
            JSONObject jsonObject = JSONObject.parseObject(postString);
            if(!jsonObject.containsKey("success")) {
                log.error("指针A=====1返回数据异常。" + postString);
                return false;
            }
            boolean success = jsonObject.getBooleanValue("success");
            if(!success){
                log.error("指针A=====2返回数据异常。" + postString);
                return false;
            }
            if(!jsonObject.containsKey("data")) {
                log.error("指针A=====3返回数据异常。" + postString);
                return false;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            if(!data.containsKey("code")) {
                log.error("指针A=====4返回数据异常。" + postString);
                return false;
            }
            String code = data.getString("code");
            if(StringUtil.isEmpty(code) || "1".equals(code)) {
                log.error("指针A=====未命中。" + postString);
                return false;
            }
        } catch (Exception e) {
            log.error("指针A查询出错", e);
            return false;
        }
        return true;
    }


}
