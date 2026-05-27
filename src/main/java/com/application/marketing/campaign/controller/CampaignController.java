package com.application.marketing.campaign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.campaign.controller.dto.MarketingClueListDTO;
import com.application.marketing.campaign.controller.dto.MarketingCreateDTO;
import com.application.marketing.campaign.controller.dto.MarketingDeleteDTO;
import com.application.marketing.campaign.controller.dto.MarketingListDTO;
import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.service.CampaignService;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;

@RestController
@RequestMapping("/marketing")
public class CampaignController {
	@Autowired
	private CampaignService campaignService;

	/**
	 * 查询营销记录（分页）
	 */
	@RequestMapping("/pageCampaign")
	public PageResult<Marketing> pageCampaign(@RequestBody MarketingListDTO dto) {
//		Sort.Direction sortDirection = Sort.Direction.fromString(dto.getSortBy());
		Pageable pageable = PageRequest.of(dto.getPageIndex()-1, dto.getPageSize());
//		Pageable pageable = PageRequest.of(dto.getPageIndex() - 1, dto.getPageSize(), Sort.by(sortDirection, dto.getSortBy()));
		Page<Marketing> result = campaignService.findAll(pageable);
		return ResultBuilder.buildPageResult(1, 12, result.getContent(), result.getTotalElements());
	}

	/**
	 * 新增营销记录
	 */
	@RequestMapping("/createCampaign")
	public RstResult<Long> createCampaign(@RequestBody MarketingCreateDTO dto) {
		Long id = campaignService.createCampaign(dto);
		if (null != id) {
			return ResultBuilder.normalResult(id);
		} else {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_CREATE);
		}
	}
	
	/**
	 * 更新营销记录
	 */
	@RequestMapping("/updateCampaign")
	public RstResult<String> updateCampaign(@RequestBody Marketing marketing) {
		campaignService.updateCampaign(marketing);
		return ResultBuilder.normalResult();
	}
	
	/**
	 * 根据ID删除
	 */
	@RequestMapping("/deleteCampaign")
	public RstResult<Void> deleteCampaign(@RequestBody MarketingDeleteDTO dto) {
		boolean r = campaignService.deleteById(dto.getCampaignId());
		if (r) {
			return ResultBuilder.normalResult();
		} else {
			return ResultBuilder.buildResult(ErrorCode.EXCEPTION_DELETE);
		}
	}
	
	/**
	 * 根据ID查询
	 */
	@GetMapping("/{id}")
	public RstResult<Marketing> getCampaign(@PathVariable Long id) {
		Marketing entity = campaignService.findById(id);
		if (null == entity) {
			return ResultBuilder.buildResult(ErrorCode.INVALID_ID);
		} else {
			return ResultBuilder.normalResult(entity);
		}
	}
	
	/**
	 * 查询营销线索（分页）
	 */
	@RequestMapping("/pageCampaignClue")
	public PageResult<MarketingClue> pageCampaignClue(@RequestBody MarketingClueListDTO dto) {
		return campaignService.pageCampaignClue(dto);
	}

	/**
	 * 批量删除
	 */
//	@DeleteMapping("/batch")
//	public ResponseEntity<Void> deleteBatch(@RequestBody List<Long> ids) {
//		marketingService.deleteAll(ids);
//		return ResponseEntity.noContent().build();
//	}

	/**
	 * 部分更新 - 只更新状态
	 */
//	@PatchMapping("/{id}/status")
//	public ResponseEntity<Integer> updateStatus(@PathVariable Long id, @RequestParam String status) {
//		int result = marketingService.updateStatus(id, status);
//		return ResponseEntity.ok(result);
//	}


	/**
	 * 根据名称查询
	 */
//	@GetMapping("/search/name")
//	public ResponseEntity<List<Marketing>> findByName(@RequestParam String name) {
//		List<Marketing> list = marketingService.findByName(name);
//		return ResponseEntity.ok(list);
//	}
//
//	/**
//	 * 根据名称模糊查询
//	 */
//	@GetMapping("/search/name-like")
//	public ResponseEntity<List<Marketing>> findByNameLike(@RequestParam String name) {
//		List<Marketing> list = marketingService.findByNameLike(name);
//		return ResponseEntity.ok(list);
//	}

	/**
	 * 根据状态查询
	 */
//	@GetMapping("/search/status")
//	public ResponseEntity<List<Marketing>> findByStatus(@RequestParam String status) {
//		List<Marketing> list = marketingService.findByStatus(status);
//		return ResponseEntity.ok(list);
//	}

	/**
	 * 根据渠道查询
	 */
//	@GetMapping("/search/channel")
//	public ResponseEntity<List<Marketing>> findByChannel(@RequestParam String channel) {
//		List<Marketing> list = marketingService.findByChannel(channel);
//		return ResponseEntity.ok(list);
//	}

}
