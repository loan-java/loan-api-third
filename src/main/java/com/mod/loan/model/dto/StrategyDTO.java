package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * loan-pay 2019/4/22 huijin.shuailijie Init
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StrategyDTO implements Serializable {
    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("score")
    private String score;

    @JsonProperty("index")
    private String index;

    @JsonProperty("ruleResList")
    private List<RuleResDTO> ruleResList;
}
