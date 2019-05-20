package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Map;

/**
 * 决策请求
 *
 * @author lijing
 * @date 2017/11/14 0014
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionReqDTO implements Serializable {
    /**
     * 商户号
     */
    @NotBlank(message = "商户号不能为空")
    @JsonProperty("member_id")
    private String member_id;
    /**
     * 决策代码
     */
    @NotBlank(message = "决策代码不能为空")
    @JsonProperty("decision_code")
    private String decision_code;
    /**
     * 商户订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    @JsonProperty("trans_id")
    private String trans_id;
    /**
     * 是否输出规则执行明细
     */
    @JsonProperty("need_details")
    private String need_details;
    /**
     * 用户基本信息
     */
    @JsonProperty("base_user")
    private BaseUserDTO base_user;

    /**
     * 异步请求回调参数
     */
    @JsonProperty("notify_url")
    private String notify_url;

    /**
     * 自定义信息
     */
    @JsonProperty("custom_params")
    private Map<String, Object> custom_params;
    /**
     * 系统参数
     */
    @JsonProperty("system_params")
    private Map<String, Object> system_params;
}
