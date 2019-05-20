package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

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
public class QjldHeTianResDTO implements Serializable {

    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("trans_id")
    private String trans_id;

    @JsonProperty("trade_no")
    private String trade_no;

    @JsonProperty("fee")
    private String fee;

    @JsonProperty("id_no")
    private String id_no;

    @JsonProperty("id_name")
    private String id_name;

    @JsonProperty("versions")
    private String versions;

    @JsonProperty("result_detail")
    private QjldHeTianResultDetailDTO result_detail;


}
