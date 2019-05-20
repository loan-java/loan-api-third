package com.mod.loan.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 风控引擎结果
 *
 * @author lijing
 * @date 2017/11/9 0009
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EngineResult<T> {

    private Boolean success;
    private T data;
    private String errorCode;
    private String errorMsg;

    public EngineResult(T data) {
        this.success = true;
        this.data = data;
    }

    public EngineResult(String errorCode, String errorMsg) {
        this.success = false;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
