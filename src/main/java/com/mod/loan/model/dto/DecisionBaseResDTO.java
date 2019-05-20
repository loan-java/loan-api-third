package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 决策基础响应类
 *
 * @author lijing
 * @date 2017/11/14 0014
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionBaseResDTO implements Serializable {
    @JsonProperty("encrypt")
    private Boolean encrypt;
    @JsonProperty("data_content")
    private DecisionResDetailDTO data_content;
}