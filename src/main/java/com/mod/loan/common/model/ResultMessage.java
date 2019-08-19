package com.mod.loan.common.model;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mod.loan.common.enums.ResponseEnum;
import org.apache.commons.lang.StringUtils;

/**
 * 消息返回
 *
 * @author wugy 2016年7月11日下午5:03:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultMessage {

    public static void main(String[] args) {
        System.out.println(new ResultMessage().toString());
        System.out.println(new ResultMessage(ResponseEnum.M2000).toString());
        System.out.println(new ResultMessage(ResponseEnum.M2000, "eeee").toString());
        System.out.println(new ResultMessage(ResponseEnum.M2000, "eeee", new Page()).toString());
    }

    /**
     * 状态
     */
    private String status;

    /**
     * 状态消息
     */
    private String msg;
    /**
     * 返回数据
     */
    private Object data;
    /**
     * 分页
     */
    private Page page;

    public ResultMessage() {
        this(null, null, null, null);
    }

    public ResultMessage(String status) {
        this(status, null, null, null);
    }

    public ResultMessage(String status, String message) {
        this(status, message, null, null);
    }

    public ResultMessage(String status, Object data) {
        this(status, null, data, null);
    }

    public ResultMessage(String status, String message, Object data) {
        this(status, message, data, null);
    }

    public ResultMessage(String status, Object data, Page page) {
        this.status = status;
        this.data = data;
        this.page = page;
    }

    public ResultMessage(String status, String msg, Object data, Page page) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.page = page;
    }

    public ResultMessage(ResponseEnum responseEnum) {
        this(responseEnum.getCode(), responseEnum.getMsg(), null, null);
    }

    public ResultMessage(ResponseEnum responseEnum, Object data) {
        this(responseEnum.getCode(), responseEnum.getMsg(), data, null);
    }

    public ResultMessage(ResponseEnum responseEnum, Object data, Page page) {
        this(responseEnum.getCode(), responseEnum.getMsg(), data, page);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public int getCode() {
        return StringUtils.isNumeric(getStatus()) ? Integer.parseInt(getStatus()) : -1;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
