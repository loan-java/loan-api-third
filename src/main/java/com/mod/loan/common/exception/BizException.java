package com.mod.loan.common.exception;

import com.mod.loan.common.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @ author liujianjian
 * @ date 2019/5/15 22:20
 */
@Slf4j
public class BizException extends Exception {

    private String code;

    public BizException() {
    }

    public BizException(String message) {
        super(message);
        this.code = ResponseEnum.M80000.getCode();
        log.error(message);
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        log.error(message);
    }

    public BizException(ResponseEnum r) {
        super(r.getMessage());
        this.code = r.getCode();
        log.error(r.getMessage());
    }

    public int getCodeInt() {
        return StringUtils.isNumeric(getCode()) ? Integer.parseInt(getCode()) : -1;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
