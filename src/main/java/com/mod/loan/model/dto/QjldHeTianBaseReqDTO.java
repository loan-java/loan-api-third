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
public class QjldHeTianBaseReqDTO {
    @NotBlank(message = "商户号不能为空")
    @JsonProperty("member_id")
    private String member_id;
    @NotBlank(message = "终端号不能为空")
    @JsonProperty("terminal_id")
    private String terminal_id;
    @NotBlank(message = "数据类型")
    @JsonProperty("data_type")
    private String data_type;
    @NotBlank(message = "加密内容不能为空")
    @JsonProperty("data_content")
    private String data_content;
}
