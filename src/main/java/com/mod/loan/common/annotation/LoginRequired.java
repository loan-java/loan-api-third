package com.mod.loan.common.annotation;

import java.lang.annotation.*;

/**
 * 
 * @author wugy 2018年1月9日 下午4:50:56
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ ElementType.METHOD }) 
@Documented 
public @interface LoginRequired {
	boolean check() default true;
}
