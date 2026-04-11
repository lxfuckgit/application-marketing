package com.application.marketing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketingApplication {
	private final static Logger logger = LoggerFactory.getLogger(MarketingApplication.class);
	
	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(MarketingApplication.class, args);
		logger.info("--------->营销活动服务启动完毕!--------");
	}
}
