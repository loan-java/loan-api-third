package com.mod.loan.util.baofoo.model;

/**
 * 预绑卡返回结果
 */
public class ReadySignVO {
    /**
     * 报文发送日期时间
     * 发送方发出本报文时的机器日期时间，如: 2017-12-19 20:19:19
     */
    private String send_time;

    /**
     * 应答报文流水号
     */
    private String msg_id;

    /**
     * 报文编号/版本号
     */
    private String version;

    /**
     * 应答码
     */
    private String resp_code;

    /**
     * 终端号
     */
    private String terminal_id;

    /**
     * 交易类型
     */
    private String txn_type;

    /**
     * 商户号
     * 宝付提供给商户的唯一编号
     */
    private String member_id;

    /**
     * 业务返回码
     */
    private String biz_resp_code;

    /**
     * 业务返回说明
     */
    private String biz_resp_msg;

    /**
     * 预签约唯一码
     * 加密方式:Base64转码后，使用数字信封指定的方式和密钥加密
     */
    private String unique_code;

    /**
     * 数字信封
     * 格式:01|对称密钥，01代表 AES
     * 加密方式:Base64 转码后使用商户的公钥加密
     */
    private String dgtl_envlp;

    /**
     * 商户保留域 1
     */
    private String req_reserved1;

    /**
     * 商户保留域 2
     */
    private String req_reserved2;

    /**
     * 系统保留域 1
     * [sign_sms]false[/sign_sms ]
     * sign_sms 节点中间是 false 代表原来宝付发送 的绑卡短信不再发送
     */
    private String additional_info1;

    /**
     * 系统保留域 2
     */
    private String additional_info2;

    /**
     * 签名域
     */
    private String signature;


    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getResp_code() {
        return resp_code;
    }

    public void setResp_code(String resp_code) {
        this.resp_code = resp_code;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getTxn_type() {
        return txn_type;
    }

    public void setTxn_type(String txn_type) {
        this.txn_type = txn_type;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getBiz_resp_code() {
        return biz_resp_code;
    }

    public void setBiz_resp_code(String biz_resp_code) {
        this.biz_resp_code = biz_resp_code;
    }

    public String getBiz_resp_msg() {
        return biz_resp_msg;
    }

    public void setBiz_resp_msg(String biz_resp_msg) {
        this.biz_resp_msg = biz_resp_msg;
    }

    public String getUnique_code() {
        return unique_code;
    }

    public void setUnique_code(String unique_code) {
        this.unique_code = unique_code;
    }

    public String getDgtl_envlp() {
        return dgtl_envlp;
    }

    public void setDgtl_envlp(String dgtl_envlp) {
        this.dgtl_envlp = dgtl_envlp;
    }

    public String getReq_reserved1() {
        return req_reserved1;
    }

    public void setReq_reserved1(String req_reserved1) {
        this.req_reserved1 = req_reserved1;
    }

    public String getReq_reserved2() {
        return req_reserved2;
    }

    public void setReq_reserved2(String req_reserved2) {
        this.req_reserved2 = req_reserved2;
    }

    public String getAdditional_info1() {
        return additional_info1;
    }

    public void setAdditional_info1(String additional_info1) {
        this.additional_info1 = additional_info1;
    }

    public String getAdditional_info2() {
        return additional_info2;
    }

    public void setAdditional_info2(String additional_info2) {
        this.additional_info2 = additional_info2;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
