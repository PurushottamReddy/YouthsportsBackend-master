package com.example.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "config.data")
@Configuration("configPropeties")
public class ConfigPropeties {
	

	private String jwtTokenExpirty;

	public String getJwtTokenExpirty() {
		return jwtTokenExpirty;
	}
	public void setJwtTokenExpirty(String jwtTokenExpirty) {
		this.jwtTokenExpirty = jwtTokenExpirty;
	}
	
}