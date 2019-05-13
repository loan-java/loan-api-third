package com.mod.loan.config.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by lijy on 2017/12/7 0007.
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

	
	@Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }
	
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
