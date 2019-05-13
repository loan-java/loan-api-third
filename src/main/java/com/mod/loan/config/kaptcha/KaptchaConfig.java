package com.mod.loan.config.kaptcha;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class KaptchaConfig {

	@Bean
	public DefaultKaptcha getDefaultKaptcha() {
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		Properties properties = new Properties();
		properties.setProperty("kaptcha.border", "no");
//		properties.setProperty("kaptcha.border.color", "105,179,90");
		properties.setProperty("kaptcha.image.width", "120");
		properties.setProperty("kaptcha.image.height", "48");
		properties.setProperty("kaptcha.textproducer.font.size", "40");
		properties.setProperty("kaptcha.textproducer.char.space", "6");
//		properties.setProperty("kaptcha.session.key", "code");
		properties.setProperty("kaptcha.noise.color", "red");
		properties.setProperty("kaptcha.background.clear.from", "255,228,225");//143,188,143
		properties.setProperty("kaptcha.background.clear.to", "255,228,225");
		properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
//		properties.setProperty("kaptcha.textproducer.font.color", "205,0,205");//205 0 205
		properties.setProperty("kaptcha.textproducer.char.length", "4");
//		properties.setProperty("kaptcha.textproducer.font.names", "Arial, Courier");
		Config config = new Config(properties);
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}