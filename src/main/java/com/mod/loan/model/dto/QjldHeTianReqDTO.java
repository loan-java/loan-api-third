package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * loan-risk 2019/4/29 huijin.shuailijie Init
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class QjldHeTianReqDTO {
    @NotBlank(message = "商户号不能为空")
    @JsonProperty("member_id")
    private String member_id;
    @NotBlank(message = "终端号不能为空")
    @JsonProperty("terminal_id")
    private String terminal_id;
    /**
     * 商户订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    @JsonProperty("trans_id")
    private String trans_id;

    @NotBlank(message = "商户订单号不能为空")
    @JsonProperty("trade_date")
    private String trade_date;
    /**
     * 身份证
     */
    @JsonProperty("id_no")
    @NotBlank(message = "身份证号码不能为空")
    private String id_no;
    /**
     * 姓名
     */
    @JsonProperty("id_name")
    @NotBlank(message = "姓名不能为空")
    private String id_name;

    @JsonProperty("product_type")
    @NotBlank(message = "产品类型")
    private String product_type;

    @JsonProperty("versions")
    @NotBlank(message = "版本号")
    private String versions;
}


