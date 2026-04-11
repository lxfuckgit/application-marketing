package com.application.marketing.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.marketing.controller.dto.HuokeLinkCreate;
import com.application.marketing.controller.dto.MarketingCreateDTO;
import com.application.marketing.domain.Marketing;
import com.application.marketing.repository.MarketingDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.enums.StatusEnum;
import com.thirdparty.params.EweixinResult;

@Service
public class CampaignService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MarketingDao marketingRepository;
	
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
				logger.warn("--->createHuokeLink方法异常：");
				return null;
			}
			Marketing entity = new Marketing();
			entity.setName(dto.getName());
			entity.setType(dto.getType());
			entity.setExtid(json.get("link_id").asText());
			entity.setLink(json.get("url").asText());
			entity.setStatus(StatusEnum.INIT.getValue());
			entity.setCreateId(String.valueOf(dto.getUserId()));
			marketingRepository.save(entity);
			return entity.getId();
		} else {
			Marketing entity = new Marketing();
			entity.setName(dto.getName());
			entity.setType(dto.getType());
			entity.setStatus(StatusEnum.INIT.getValue());
			entity.setCreateId(String.valueOf(dto.getUserId()));
			marketingRepository.save(entity);
			return entity.getId();
		}
	}
	
	public Marketing updateCampaign(Marketing marketing) {
		//先检查是否存在
		if (!marketingRepository.existsById(marketing.getId())) {
			throw new RuntimeException("营销记录不存在，ID: " + marketing.getId());
		}
		return marketingRepository.save(marketing);
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

//	public void deleteAll(List<Long> ids) {
//		marketingRepository.deleteAllById(ids);
//	}

//	public List<Marketing> saveAll(List<Marketing> marketingList) {
//		return marketingRepository.saveAll(marketingList);
//	}
	
//	public List<Marketing> findAll() {
//		return marketingRepository.findAll();
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
