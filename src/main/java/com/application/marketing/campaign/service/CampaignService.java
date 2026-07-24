package com.application.marketing.campaign.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.marketing.campaign.controller.dto.MarketingClueListDTO;
import com.application.marketing.campaign.controller.dto.MarketingCreateDTO;
import com.application.marketing.campaign.controller.dto.MarketingListDTO;
import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.domain.MarketingParty;
import com.application.marketing.campaign.domain.MarketingTag;
import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.campaign.repository.MarketingPartyDao;
import com.application.marketing.campaign.repository.MarketingTagDao;
import com.application.marketing.common.service.QywxAcquisitionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;
import com.javapai.framework.enums.StatusEnum;
import com.thirdparty.params.EweixinResult;

@Service
public class CampaignService extends AbstractBizService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MarketingDao marketingRepository;
	
	@Autowired
	MarketingPartyDao marketingPartyDao;

	@Autowired
	MarketingTagDao marketingTagDao;
	
	@Autowired
	QyWeixinService qyWeixinService;
	
	@Autowired
	QywxAcquisitionService acquisitionService;

	@Transactional
	public Long createCampaign(MarketingCreateDTO dto) {
		/* 创建营销活动 */
		Marketing entity = new Marketing();
		entity.setAppId(dto.getAppId());
		entity.setName(dto.getName());
		entity.setType(dto.getType());
		entity.setAdAccount(dto.getAdAccount());
		entity.setStatus(StatusEnum.INIT.getValue());
		entity.setCreateId(String.valueOf(dto.getUserId()));
		marketingRepository.save(entity);
		logger.info("--->营销活动创建结果：{}", entity.getId());
		/* 创建营销活动-竞价人员 */
		if (StringUtils.isNotBlank(dto.getBidderId())) {
			MarketingParty party = new MarketingParty();
			party.setMarketingId(entity.getId());
			party.setPartyType(9);
			party.setPartyId(dto.getBidderId());
			marketingPartyDao.save(party);
		}
		/* 创建营销活动-关联人员 */
		dto.getStaffList().forEach(staffId -> {
			MarketingParty party = new MarketingParty();
			party.setMarketingId(entity.getId());
			party.setPartyType(1);
			party.setPartyId(staffId);
			marketingPartyDao.save(party);
		});
		/* 创建营销活动-关联标签 */
		dto.getTagList().forEach(tagId -> {
			MarketingTag mtag = new MarketingTag();
			mtag.setMarketingId(entity.getId());
			mtag.setTagType(0);
			mtag.setTagValue(tagId);
			marketingTagDao.save(mtag);
			logger.info("--->营销活动关联标签创建结果：{}", mtag.getId());
		});
		
		/* 特殊的类型处理 */
		if (Marketing.TYPE_8 == dto.getType()) {
			JsonNode json = acquisitionService.createHuokeLink(dto.getAppId(), dto.getName(), dto.getStaffList(), false);
			if (null == json || null == json.get("link_id")) {
				logger.warn("--->[]企业微信createHuokeLink方法异常，中断部分业务。", entity.getId());
				return entity.getId();
			}
			entity.setExtid(json.get("link_id").asText());
//			entity.setLink(json.get("url").asText());
			entity.setLink(json.get("url").asText() + "?customer_channel=" + json.get("link_id").asText());
			// 变更状态：ENABLE
			entity.setStatus(StatusEnum.ENABLE.getValue());
			marketingRepository.save(entity);
			logger.info("--->营销活动更新结果：{}", entity.getId());
		}
		return entity.getId();
	}
	
	public boolean updateCampaign(Marketing marketing) {
		//先检查是否存在
		java.util.Optional<Marketing> optional = marketingRepository.findById(marketing.getId());
		if(optional.isEmpty()) {
			throw new RuntimeException("营销记录不存在，ID: " + marketing.getId());
		}
		if (Marketing.TYPE_8 == optional.get().getType()) {
			if (StringUtils.isBlank(optional.get().getExtid())) {
				logger.info("--->营销活动关联外部标识为空，已跳过外部操作！");
				return true;
			}
			List<String> partyList = marketingPartyDao.listPartyIdByMarketingId(optional.get().getId());
			EweixinResult result = acquisitionService.updateHuokeLink(optional.get().getAppId(), optional.get().getExtid(), optional.get().getName(), partyList);
			if (!result.ifSuccess()) {
				logger.warn("--->更新操作异常：官方操作返回异常！");
				return false;
			}
		}
		marketingRepository.save(marketing);
		logger.info("--->营销活动更新完成！");
		return true;
	}

	public boolean deleteById(Long id) {
		Optional<Marketing> optional = marketingRepository.findById(id);
		if (!optional.isPresent()) {
			logger.warn("--->删除操作异常：非法数据标识！");
			return false;
		}
		if (Marketing.TYPE_8 == optional.get().getType()) {
			if (StringUtils.isNotBlank(optional.get().getExtid())) {
				EweixinResult result = acquisitionService.deleteHuokeLink(optional.get().getAppId(), optional.get().getExtid());
				if (!result.ifSuccess()) {
					logger.warn("--->删除操作异常：官方操作返回异常！");
					return false;
				}
			} else {
				logger.info("--->营销活动关联外部标识为空，已跳过外部操作！");
			}
		}
		marketingRepository.deleteById(id);
		return true;
	}

	public Marketing findById(Long id) {
		Optional<Marketing> optional = marketingRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			return null;
		}
	}
	
	public Page<Marketing> findAll(Pageable pageable) {
		return marketingRepository.findAll(pageable);
	}
	
	public PageResult<Marketing> pageCampaign(MarketingListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select * from marketing where 1=1");
		if (null != dto.getType()) {
			sb.append(" and type=?");
			params.add(dto.getType());
		}
		if (StringUtils.isNotBlank(dto.getName())) {
			sb.append(" and name like ?");
			params.add("%" + dto.getName() + "%");
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), Marketing.class);
	}
	
	public PageResult<MarketingClue> pageCampaignClue(MarketingClueListDTO dto) {
		List<Object> params = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select * from marketing_clue where 1=1");
		if (StringUtils.isNotBlank(dto.getIntUserId())) {
			sb.append(" and user_id=?");
			params.add(dto.getIntUserId());
		}
		if (StringUtils.isNotBlank(dto.getExtUserId())) {
			sb.append(" and ext_user_id=?");
			params.add(dto.getExtUserId());
		}
		if (StringUtils.isNotBlank(dto.getExtUserName())) {
			sb.append(" and ext_user_name=?");
			params.add(dto.getExtUserName());
		}
		if (StringUtils.isNotBlank(dto.getExposure‌Id())) {
			sb.append(" and exposure‌_id=?");
			params.add(dto.getExposure‌Id());
		}
		if (null != dto.getCreateTime() && dto.getCreateTime().length == 2) {
			sb.append(" and (create_time>=? and create_time<=?)");
			params.add(dto.getCreateTime()[0]);
			params.add(dto.getCreateTime()[1]);
		}
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), MarketingClue.class);
	}
	
	public boolean updateStatusByExtId(String extId, String status) {
		Marketing entity = marketingRepository.findByExtid(extId);
		if (null == entity) {
			logger.warn("--->删除操作异常：非法数据标识！");
			return false;
		}
		entity.setStatus(status);
		marketingRepository.save(entity);
		return true;
	}
	
}
