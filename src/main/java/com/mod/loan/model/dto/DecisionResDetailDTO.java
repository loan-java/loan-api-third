package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 决策流处理结果
 *
 * @author lijing
 * @date 2017/11/14 0014
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionResDetailDTO implements Serializable {

    @JsonProperty("decision_no")
    private String decision_no;
    @JsonProperty("trans_id")
    private String trans_id;
    @JsonProperty("orderStatus")
    private String orderStatus;

    @JsonProperty("order_money")
    private Long order_money;
    @JsonProperty("fee")
    private Boolean fee;
    @JsonProperty("custom_grade")
    private List<Object> custom_grade;

    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("res_score")
    private Double resScore;

    @JsonProperty("strategies")
    private List<StrategyDTO> strategies;

}
