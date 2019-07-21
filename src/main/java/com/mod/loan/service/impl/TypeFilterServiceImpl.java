package com.mod.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.config.Constant;
import com.mod.loan.mapper.TypeFilterMapper;
import com.mod.loan.model.TypeFilter;
import com.mod.loan.model.User;
import com.mod.loan.service.TypeFilterService;
import com.mod.loan.util.DateUtil;
import com.mod.loan.util.baofoo.rsa.RsaCodingUtil;
import com.mod.loan.util.baofoo.util.SecurityUtil;
import com.mod.loan.util.rongze.RongZeRequestUtil;
import com.mod.loan.util.typeA.HttpUtils;
import com.mod.loan.util.typeA.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
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
public class TypeFilterServiceImpl implements TypeFilterService {

    @Resource
    private TypeFilterMapper typeFilterMapper;

    /**
     * 获取是否是黑名单 true--黑名单  false--不是黑名单
     */
    @Override
    public void getInfoByTypeA(User user, String orderNo) {
        TypeFilter typeFilter = new TypeFilter();
        try {
            typeFilter.setOrderNo(orderNo);
            typeFilter.setType(1);
            typeFilter = typeFilterMapper.selectOne(typeFilter);
            if (typeFilter != null) {
                log.info("指针A=====当前订单已经探针过" + orderNo);
                return;
            }
            typeFilter = new TypeFilter();
            typeFilter.setOrderNo(orderNo);
            typeFilter.setType(1);
            typeFilter.setUid(user.getId());
            typeFilter.setCreateTime(new Date());
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
            String pfxpath = Constant.typeaPfxName;// 商户私钥 正式
//                       String pfxpath = "E://other_project/key-typea/8000013189_pri.pfx";// 商户私钥

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
            Boolean flag = resultTypeA(postString); //false-不是黑名单，true-是黑名单

            typeFilter.setResult(flag.toString());
            typeFilter.setResultlStr(postString);
            typeFilterMapper.insert(typeFilter);
        } catch (Exception e) {
            log.error("指针A查询出错", e);
            return;
        }
        return;
    }

    /**
     * 获取返回结果
     *
     * @param postString
     * @return false-不是黑名单，true-是黑名单
     */
    public Boolean resultTypeA(String postString) {
        try {
            /** ================处理返回结果============= **/
            if (postString == null || postString.isEmpty()) {// 判断参数是否为空
                log.error("指针A=====1返回数据为空" + postString);
                return false;
            } else {
                JSONObject jsonObject = JSONObject.parseObject(postString);
                if (!jsonObject.containsKey("success")) {
                    log.error("指针A=====2返回数据异常。" + postString);
                    return false;
                }
                boolean success = jsonObject.getBooleanValue("success");
                if (!success) {
                    log.error("指针A=====3返回数据异常。" + postString);
                    return false;
                }
                //判断 data 是否有值
                if (!jsonObject.containsKey("data")) {
                    log.error("指针A=====4返回数据异常。" + postString);
                    return false;
                }
                JSONObject data = jsonObject.getJSONObject("data");
                if (!data.containsKey("code")) {
                    log.error("指针A=====5返回数据异常。" + postString);
                    return false;
                }
                //判断 code 得值
                String code = data.getString("code");
                if (StringUtil.isEmpty(code)) {
                    log.error("指针A=====6返回数据异常。" + postString);
                    return false;
                }
                if ("1".equals(code)) {
                    return false;
                }
                //判断result_detail得值
                if (!data.containsKey("result_detail")) {
                    log.error("指针A=====7返回数据异常。" + postString);
                    return false;
                }
                JSONObject resultDetail = data.getJSONObject("result_detail");
                if (!resultDetail.containsKey("result_code")) {
                    log.error("指针A=====8返回数据异常。" + postString);
                    return false;
                }
                String resultCode = resultDetail.getString("result_code");
                if (StringUtil.isEmpty(resultCode)) {
                    log.error("指针A=====9返回数据异常。" + postString);
                    return false;
                }
                if ("1".equals(resultCode)) {
                    return true;
                }
                if ("4".equals(resultCode)) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("指针A查询出错", e);
            return false;
        }
        return false;
    }


    /**
     * t
     *
     * @return
     */
    @Override
    public void guize(User user, String orderNo) {
        TypeFilter typeFilter = new TypeFilter();
        try {
            typeFilter.setOrderNo(orderNo);
            typeFilter.setType(2);
            typeFilter = typeFilterMapper.selectOne(typeFilter);
            if (typeFilter != null) {
                log.info("规则集=====当前订单已经规则集过" + orderNo);
                return;
            }
            typeFilter = new TypeFilter();
            typeFilter.setOrderNo(orderNo);
            typeFilter.setType(2);
            typeFilter.setUid(user.getId());
            typeFilter.setCreateTime(new Date());
            String guizeResult = this.guizeInfo(orderNo, user);
            log.info("订单号:{},规则集结果:{}", orderNo, guizeResult);
            if (guizeResult == null) {
                typeFilter.setResult("false");
            } else {
                typeFilter.setResult("true");
            }
            typeFilter.setResultlStr(guizeResult);
            typeFilterMapper.insert(typeFilter);
        } catch (Exception e) {
            log.error("规则集出错", e);
        }
    }


    /**
     * null-通过（解析出错），非null-拒绝
     *
     * @return
     */
    public String guizeInfo(String orderNo, User user) {
        try {
            //身份证有效期小于90天
            long less = DateUtil.betweenDaysInDate(DateUtil.getTodayShort(), user.getIndate(), "yyyyMMdd");
            if (90 > less) {
                return "身份证有效期小于90天，实际：" + less;
            }
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("order_no", orderNo);
            jsonObject1.put("type", "2");
            String mxMobile = RongZeRequestUtil.doPost(Constant.rongZeQueryUrl, "api.charge.data", jsonObject1.toJSONString());
            JSONObject jsonObject = JSONObject.parseObject(mxMobile);
            String dataStr = jsonObject.getString("data");
            JSONObject all = JSONObject.parseObject(dataStr);
            JSONObject data = all.getJSONObject("data");
            JSONObject report = data.getJSONObject("report");
            JSONArray applicationCheck = report.getJSONArray("application_check");
            //todo 暂时没法决定 是否模拟器

            //手机注册小于180天
            JSONObject cellPhone = applicationCheck.getJSONObject(2);
            JSONObject cellPhoneCheckPoints = cellPhone.getJSONObject("check_points");
            String regTime = cellPhoneCheckPoints.getString("reg_time");
            long lessDayLong = DateUtil.betweenDaysInDate(regTime, DateUtil.dateToStrLong(new Date()), "yyyy-MM-dd HH:mm:ss");
            if (180 > lessDayLong) {
                return "手机注册小于180天，实际：" + lessDayLong;
            }
            //静默次数
            JSONArray behaviorCheck = report.getJSONArray("behavior_check");
            JSONObject jingmo = behaviorCheck.getJSONObject(2);
            int jimoscore = jingmo.getInteger("score");
            if (2 == jimoscore) {
                return "手机静默情况较多，实际：" + jingmo;
            }
            //通讯录人数少于120
            JSONObject userInfoCheck = report.getJSONObject("user_info_check");
            JSONObject checkblackInfo = userInfoCheck.getJSONObject("check_black_info");
            Integer collectionContactConut = checkblackInfo.getInteger("contacts_class1_cnt");
            if (120 > collectionContactConut) {
                return "通讯录人数少于120人，实际：" + collectionContactConut;
            }
            //用户通话数记录数少于15
            JSONArray contactList = report.getJSONArray("contact_list");
            if (15 > contactList.size()) {
                return "用户通话数记录数少于15次，实际：" + contactList.size();
            }
            //年龄小于20或大于45		拒绝
            JSONObject idCard = applicationCheck.getJSONObject(1);
            JSONObject idCardCheckPoints = idCard.getJSONObject("check_points");
            int idCardAge = idCardCheckPoints.getInteger("age");
            if (idCardAge < 20 || idCardAge > 45) {
                return "年龄小于20岁或大于45岁，实际：" + idCardAge;
            }
        } catch (Exception e) {
            log.error("规则集出错", e);
            return e.toString();
        }
        return null;
    }


    public static void main(String[] args) {
        long less = DateUtil.betweenDaysInDate(DateUtil.getTodayShort(), "20201220", "yyyyMMdd") - 3 * 30;
        System.out.println(less);

    }
}
