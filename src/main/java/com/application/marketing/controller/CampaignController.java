package com.application.marketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.controller.dto.MarketingCreateDTO;
import com.application.marketing.controller.dto.MarketingListDTO;
import com.application.marketing.domain.Marketing;
import com.application.marketing.service.CampaignService;
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
	 * 分页查询
	 */
	@RequestMapping("/pageCampaign")
	public PageResult<Marketing> pageCampaign(@RequestBody MarketingListDTO dto) {
//		Sort.Direction sortDirection = Sort.Direction.fromString(direction);
		Pageable pageable = PageRequest.of(dto.getPageIndex()-1, dto.getPageSize());
//		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
		Page<Marketing> result = campaignService.findAll(pageable);
		return ResultBuilder.buildPageResult(1, 12, result.getContent(), result.getTotalElements());
	}

	/**
	 * 新增营销记录
	 */
	@RequestMapping("/createCampaign")
	public RstResult<Long> createCampaign(@RequestBody MarketingCreateDTO dto) {
		Long id = campaignService.createCampaign(dto);
		return ResultBuilder.normalResult(id);
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
	@DeleteMapping("/{id}")
	public RstResult<Void> delete(@PathVariable Long campaignId) {
		boolean r = campaignService.deleteById(campaignId);
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
	 * 查询所有
	 */
//	@GetMapping
//	public ResponseEntity<List<Marketing>> findAll() {
//		List<Marketing> list = marketingService.findAll();
//		return ResponseEntity.ok(list);
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
