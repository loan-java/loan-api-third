package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 规则处理结果
 *
 * @author lijing
 * @date 2017/11/7 0007
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleResDTO implements Serializable {
    /** 执行结果代码 */
    @JsonProperty("code")
    private String code;
    /** 执行结果描述 */
    @JsonProperty("desc")
    private String desc;
    /** 规则id */
    @JsonProperty("score")
    private String score;
    /** 规则描述 */
    @JsonProperty("labelValue")
    private Boolean labelValue;

    @JsonProperty("isDefaultVal")
    private String isDefaultVal;

    /** 规则得分 */
    @JsonProperty("rule_name")
    private String rule_name;

    /** 规则得分 */
    @JsonProperty("rule_id")
    private String rule_id;

}
