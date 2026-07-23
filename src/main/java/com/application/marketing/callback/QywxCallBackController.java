package com.application.marketing.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.callback.aes.AesException;
import com.application.marketing.callback.aes.WXBizMsgCrypt;
import com.application.marketing.callback.qywx.CustomerAcquisitionEvent;
import com.application.marketing.callback.qywx.ExternalContactEvent;
import com.application.marketing.common.domain.ThirdAccount;
import com.application.marketing.common.repository.ThirdAccountDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 企业微信回调通知。<br>
 * 
 * 如何配置企微回调：<br>
 * 1、登录企业微信管理后台：https://work.weixin.qq.com/<br>
 * 2、进入「应用管理」→「应用」→「自建」→「创建应用」创建自己的应用<br>
 * 3、进入自建应用的详情页后在功能区的【接收消息】下设置回调地址(回调域名主体需要与企微主体一致）。<br>
 * 4、进入「客户与上下游」→「客户联系」→「API」→「可调用应用」；然后将你创建的应用添加到列表中。<br>
 * 5、保存配置，测试是否回调成功。<br>
 */
@RestController
@RequestMapping("/qywx")
public class QywxCallBackController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final XmlMapper xmlMapper = new XmlMapper();
	
	@Autowired
	ThirdAccountDao thirdAccountDao;

	@Autowired
	ExternalContactEvent externalContactEvent;

	@Autowired
	CustomerAcquisitionEvent customerAcquisitionEvent;
	
	/**
	 * GET回调事件（用于验证URL地址）
	 * 
	 * @return
	 */
	@GetMapping("/callbackEvent/{appId}")
	public String callbackVerifyUrl(@PathVariable("appId") String appId,
			@RequestParam("msg_signature") String msgSignature,
			@RequestParam("timestamp") String timestamp,
			@RequestParam("nonce") String nonce,
			@RequestParam("echostr") String echostr) {
		
		ThirdAccount account = thirdAccountDao.findByAppId(appId);
		if (null == account) {
			logger.error("--->回调验证失败，[{}]配置信息有误！ {}", appId);
			return "error";
		}
		
		try {
			String token = account.getCallbackToken();
			String encodingAESKey = account.getCallbackKey();
			String corpId = account.getAppId();
			// 1. 创建加解密对象（企业内部应用场景，第三个参数ReceiveId为CorpId）
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
			// 2. 解密echostr，得到明文消息
			String sEchoStr = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
			// 3. 返回解密后的明文字符串（注意：不要加引号、换行符等任何多余内容）
			logger.info("--->验证完成：{}", sEchoStr);
			return sEchoStr;
		} catch (Exception e) {
			logger.error("验证URL失败: {}", e.getMessage());
			return "error";
		}
	}

	/**
	 * POST回调事件（用于接收MSG消息）
	 * 
	 * @return
	 */
	@PostMapping("callbackEvent/{appId}")
	public String callbackReceiveMsg(@PathVariable("appId") String appId, @RequestParam("msg_signature") String msgSignature, @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestBody String requestBody) {
		logger.info("--->收到回调事件, msg_signature={}, timestamp={}, nonce={}", msgSignature, timestamp, nonce);
		ThirdAccount account = thirdAccountDao.findByAppId(appId);
		if (null == account) {
			logger.error("--->回调处理失败，[{}]配置信息有误！ {}", appId);
			return "error";
		}
		
		try {
			// 1. 创建加解密对象
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(account.getCallbackToken(), account.getCallbackKey(), appId);
			// 2. 从请求体XML中提取Encrypt标签的内容，并解密
			String plainText = wxcpt.DecryptMsg(msgSignature, timestamp, nonce, requestBody);

			// 3. 解析解密后的明文XML，处理业务逻辑（如记录消息、自动回复等）
			logger.info("收到解密后的消息: {}", plainText);
			
			// 4. 解析xml内容，获取MsgType、Event等字段进行判断
			JsonNode xmlcontent = xmlMapper.readValue(plainText, JsonNode.class);
			if ("delete_link".equals(xmlcontent.get("ChangeType").asText())) {
				customerAcquisitionEvent.eventDeleteLink(xmlcontent.get("LinkId").asText());
			} else if ("friend_request".equals(xmlcontent.get("ChangeType").asText())) {
				String linkId = xmlcontent.get("LinkId").asText();
				String state = xmlcontent.get("State").asText();
				customerAcquisitionEvent.eventFriendRequest(linkId, state);
			} else if ("add_external_contact".equals(xmlcontent.get("ChangeType").asText())) {
				String state = null;
				if (xmlcontent.has("State")) {
					state = xmlcontent.get("State").asText();
				}
				String userId = xmlcontent.get("UserID").asText();
				String extUserId = xmlcontent.get("ExternalUserID").asText();
				externalContactEvent.eventAddExternalContact(appId, userId, extUserId, state);
			} else if ("add_half_external_contact".equals(xmlcontent.get("ChangeType").asText())) {
				String linkId = xmlcontent.get("State").asText();
				String userId = xmlcontent.get("UserID").asText();
				String extUserId = xmlcontent.get("ExternalUserID").asText();
				externalContactEvent.eventAddHalfExternalContact(linkId, userId, extUserId);
			}else if("customer_start_chat".equals(xmlcontent.get("ChangeType").asText())){
				String linkId = xmlcontent.get("LinkId").asText();
				String userId = xmlcontent.get("UserID").asText();
				String externalUserID = xmlcontent.get("ExternalUserID").asText();
				customerAcquisitionEvent.eventCustomerStartChat(linkId, userId, externalUserID);
			}else if ("message_from_customer".equals(xmlcontent.get("ChangeType").asText())) {
//				String corpId = xmlcontent.get("CorpID").asText();
				String LinkId = xmlcontent.get("LinkId").asText();
				String chatKey = xmlcontent.get("ChatKey").asText();
				customerAcquisitionEvent.eventMessageFromCustomer(LinkId, chatKey);
			}else {
				logger.info("--->当前类型({})暂无处理...", xmlcontent.get("ChangeType").asText());
			}
			// 5. 普通业务事件回调结果返回字符串：success；提示：当返回非success企微也不会二次回调。
			return "success";

		} catch (AesException e) {
			logger.error("--->回调消息解密失败: {}", e.getMessage());
			return "error";
		} catch (JsonProcessingException e) {
			logger.error("--->回调消息转换失败: {}", e.getMessage());
//			e.printStackTrace();
			return "error";
		}
	}
	
}
