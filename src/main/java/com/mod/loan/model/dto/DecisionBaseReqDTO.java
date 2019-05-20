package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 决策引擎基础请求
 *
 * @author lijing
 * @date 2018/4/16 0016.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionBaseReqDTO implements Serializable {
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
    @JsonProperty("res_encrypt")
    @NotNull(message = "响应内容是否加密不能为空")
    private Boolean res_encrypt;
    @NotBlank(message = "加密内容不能为空")
    @JsonProperty("data_content")
    private String data_content;

}
