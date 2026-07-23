package com.application.marketing.component;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.javapai.framework.utils.UtilHttp;

@Component
public class DataNotice {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String url = "https://dj.lemanman.cn/admin-api/lpg/qiwei/create";

	@Async
	public void doDateNotice(String exposure‌Id, Integer exposure‌Status) {
		//0-发送了好友请求，1-添加了好友，2-首次发送消息-后面的就是多次发送消息）
		String result = UtilHttp.jsonPost(url, Map.of("state", exposure‌Id, "recvMsgCnt", exposure‌Status));
		logger.info("--->[{}]数据通知结果：{}", exposure‌Id, result);
	}

}
