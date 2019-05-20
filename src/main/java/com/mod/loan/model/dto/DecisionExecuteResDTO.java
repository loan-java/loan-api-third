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
public class DecisionExecuteResDTO implements Serializable {
    @JsonProperty("decision_no")
    private String decision_no;

    @JsonProperty("trans_id")
    private String trans_id;

    @JsonProperty("order_money")
    private Long order_money;

    @JsonProperty("orderStatus")
    private String orderStatus;

    @JsonProperty("fee")
    private boolean fee;

    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("res_score")
    private Double res_score;

    @JsonProperty("strategies")
    private List<StrategyDTO> strategies;
}
