package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 基本用户信息
 *
 * @author lijing
 * @date 2017/11/7 0007
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseUserDTO implements Serializable{
    /** 身份证 */
    @JsonProperty("id_card")
    @NotBlank(message = "身份证号码不能为空")
    private String id_card;
    /** 姓名 */
    @JsonProperty("id_name")
    @NotBlank(message = "姓名不能为空")
    private String id_name;
    /** 手机号码 */
    @JsonProperty("phone")
    @NotBlank(message = "手机号码不能为空")
    private String phone;
    /** 银行卡卡号 */
    @JsonProperty("bank_card_no")
    @NotBlank(message = "银行卡号不能为空")
    private String bank_card_no;

    /** 设备指纹 */
    @JsonProperty("device_no")
    @Length(max = 32, message = "设备指纹号不能超过32位")
    private String device_no;
    /** 所在城市 */
    @JsonProperty("city")
    private String city;
    /** 家庭地址 */
    @JsonProperty("address")
    private String address;
    /** 性别 */
    @JsonProperty("sex")
    @Pattern(regexp = "(M|W)",message = "性别传值不正确(M:男|W:女)")
    private String sex;
    /** 年龄 */
    @JsonProperty("age")
    private Integer age;
    /** IP */
    @JsonProperty("ip")
    private String ip;

}
