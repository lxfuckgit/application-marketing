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
import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.domain.MarketingParty;
import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.campaign.repository.MarketingPartyDao;
import com.application.marketing.controller.dto.HuokeLinkCreate;
import com.application.marketing.service.QyWeixinService;
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
	QyWeixinService qyWeixinService;

	@Transactional
	public Long createCampaign(MarketingCreateDTO dto) {
		if (Marketing.TYPE_8 == dto.getType()) {
			HuokeLinkCreate huokeLink = new HuokeLinkCreate();
			huokeLink.setLinkName(dto.getName());
			huokeLink.setStaffList(dto.getStaffList());
			JsonNode json = qyWeixinService.createHuokeLink(huokeLink);
			if (null == json || null == json.get("link_id")) {
				logger.warn("--->企业微信createHuokeLink方法异常：");
				return null;
			}
			Marketing entity = new Marketing();
			entity.setName(dto.getName());
			entity.setType(dto.getType());
			entity.setExtid(json.get("link_id").asText());
			entity.setLink(json.get("url").asText());
			if (StringUtils.isBlank(dto.getAdAccount())) {
				entity.setLink(json.get("url").asText());
			} else {
				entity.setLink(json.get("url").asText() + "?customer_channel=" + dto.getAdAccount());
			}
			entity.setAdAccount(dto.getAdAccount());
			entity.setStatus(StatusEnum.INIT.getValue());
			entity.setCreateId(String.valueOf(dto.getUserId()));
			marketingRepository.save(entity);
			logger.info("--->营销活动创建结果：{}", entity.getId());
			dto.getStaffList().forEach(staffId -> {
				MarketingParty party = new MarketingParty();
				party.setMarketingId(entity.getId());
				party.setPartyType(1);
				party.setPartyId(staffId);
				marketingPartyDao.save(party);
			});
			return entity.getId();
		} else {
			Marketing entity = new Marketing();
			entity.setName(dto.getName());
			entity.setType(dto.getType());
			entity.setAdAccount(dto.getAdAccount());
			entity.setStatus(StatusEnum.INIT.getValue());
			entity.setCreateId(String.valueOf(dto.getUserId()));
			marketingRepository.save(entity);
			return entity.getId();
		}
	}
	
	public Integer updateCampaign(Marketing marketing) {
		//先检查是否存在
		java.util.Optional<Marketing> optional = marketingRepository.findById(marketing.getId());
		if(optional.isEmpty()) {
			throw new RuntimeException("营销记录不存在，ID: " + marketing.getId());
		}
		if (StringUtils.isNotBlank(marketing.getAdAccount())) {
			if (!marketing.getAdAccount().equals(optional.get().getAdAccount())) {
				String link = optional.get().getLink().split("?")[0];
				optional.get().setLink(link + "?customer_channel=" + marketing.getAdAccount());
			}
		}
		marketingRepository.save(marketing);
		return 1;
	}

	public boolean deleteById(Long id) {
		Optional<Marketing> optional = marketingRepository.findById(id);
		if (!optional.isPresent()) {
			logger.warn("--->删除操作异常：非法数据标识！");
			return false;
		}
		if (Marketing.TYPE_8 == optional.get().getType()) {
			EweixinResult result = qyWeixinService.deleteHuokeLink(optional.get().getExtid());
			if (!result.ifSuccess()) {
				logger.warn("--->删除操作异常：官方操作返回异常！");
				return false;
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
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), MarketingClue.class);
	}

//	public void deleteAll(List<Long> ids) {
//		marketingRepository.deleteAllById(ids);
//	}

//	public List<Marketing> saveAll(List<Marketing> marketingList) {
//		return marketingRepository.saveAll(marketingList);
//	}
	
//	public List<Marketing> findByName(String name) {
//		return marketingRepository.findByName(name);
//	}
//
//	public List<Marketing> findByStatus(String status) {
//		return marketingRepository.findByStatus(status);
//	}
//
//	public List<Marketing> findByChannel(String channel) {
//		return marketingRepository.findByChannel(channel);
//	}
//
//	public List<Marketing> findByNameLike(String name) {
//		return marketingRepository.findByNameContaining(name);
//	}
//
//	public int updateStatus(Long id, String status) {
//		return marketingRepository.updateStatusById(status, id);
//	}

}
