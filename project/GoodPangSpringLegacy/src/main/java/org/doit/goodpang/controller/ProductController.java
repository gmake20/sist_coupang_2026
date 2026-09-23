package org.doit.goodpang.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.doit.goodpang.domain.ProductDTO;
import org.doit.goodpang.domain.ProductImageDTO;
import org.doit.goodpang.domain.ProductOptionDTO;
import org.doit.goodpang.service.ProductOptionService;
import org.doit.goodpang.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	private final ProductOptionService productOptionService;

	/** ① 경로형 — /product/131 */
	@GetMapping("/product/{productNo}")
	public String detailByPath(
			@PathVariable("productNo") int productNo,
			@RequestParam(value = "optionId", required = false) String optionId,
			Model model,
			HttpServletResponse response) throws Exception {

		return detail(productNo, optionId, model, response);
	}

	 /**
     * ② 옛 주소 — /product?productNo=131
     *
     * ★ 아직 필요함: 이미 옮긴 inc/header.jsp 789줄·1187줄(장바구니 미리보기)이 이 주소를 씀.
     *   헤더는 전 페이지에 뜨므로 지우면 바로 404. 구버전 전체로는 17곳.
     *   팀원 JSP 가 다 옮겨오고 링크를 경로형으로 정리하면 이 메서드만 지우면 됨.
     */
	@GetMapping(value = "/product", params = "productNo")
	public String detailByParam(
			@RequestParam("productNo") int productNo,
			@RequestParam(value = "optionId", required = false) String optionId,
			Model model,
			HttpServletResponse response) throws Exception {

		return detail(productNo, optionId, model, response);
	}

	/** ①②가 공통으로 쓰는 본체 */
	private String detail(int productNo, String optionId, Model model, HttpServletResponse response)
			throws Exception {

		ProductDTO product = productService.getProduct(productNo);

		if (product == null) {
			// 여기서 응답을 끝냈으므로 뷰로 안 감 (구버전 Handler 가 null 리턴하던 것과 같음)
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "상품을 찾을 수 없습니다.");
			return null;
		}

		model.addAttribute("p", product);

		// 구버전 product.jsp 16줄의 <title>${p.productName}-${p.subCategoryName}|굿팡</title>.
        // head 가 layout_shop.jsp 로 올라가면서 여기서 넘겨줌
        model.addAttribute("pageTitle",
                        product.getProductName() + "-" + product.getSubCategoryName() + "|굿팡");

        /*
         * 2026-09-06 — 판매중지 상품 처리. 구버전은 product.jsp 의 <body> 에 직접 썼는데,
         * Tiles 로 오면서 <body> 가 layout_shop.jsp 로 올라가서 여기서 값만 넘김.
         *   ① is-soldout : 품절과 같은 CSS 를 그대로 재사용(구매 버튼이 품절 버튼으로 바뀜, product.css 9장).
         *      옵션마다 다른 품절과 달리 판매중지는 상품 전체라 처음부터 붙여둠.
         *   ② saleStatus : product.js 가 옵션을 바꿀 때마다 is-soldout 을 다시 계산해서 덮어쓰기 때문에
         *      (product.js:466) 자바스크립트도 이 값을 보고 판매중지면 품절 상태를 유지하게 함.
         */
        model.addAttribute("bodyClass", "page-product"
                        + ("판매 중지".equals(product.getSaleStatus()) ? " is-soldout" : ""));
        model.addAttribute("saleStatus", product.getSaleStatus());
		model.addAttribute("deliveryDate", productService.calcDeliveryDate());

		// 옵션 — URL 의 optionId 가 이 상품 것이 아니면 조용히 첫 옵션으로
		List<ProductOptionDTO> options = productService.getOptions(productNo);
		ProductOptionDTO selectedOption = productService.chooseOption(options, optionId);
		model.addAttribute("mainOption", selectedOption);

		// 가격 — 옵션 추가금까지 더한 값 기준
		int displayPrice = productService.calcDisplayPrice(product, selectedOption);
		Integer displayNormalPrice = productService.calcDisplayNormalPrice(product, selectedOption, displayPrice);

		model.addAttribute("displayPrice", displayPrice);
		model.addAttribute("displayNormalPrice", displayNormalPrice);
		model.addAttribute("discountRate", productService.calcDiscountRate(displayPrice, displayNormalPrice));
		model.addAttribute("rewardCash", productService.calcRewardCash(displayPrice));

		// 사진 — 옵션 전용 사진은 각 옵션에 붙고, 상세설명용만 따로 나옴
		List<ProductImageDTO> detailImages =
				productService.attachImagesAndGetDetailImages(productNo, options);

		model.addAttribute("options", options);
		model.addAttribute("detailImages", detailImages);
		model.addAttribute("optionsJson", productService.toOptionsJson(options));

		/*
		 * ★ 리뷰는 이번 이전 범위에서 제외 (ReviewDAO 2,019줄/SQL 31개, 담당 jihoonlee).
		 *   product.jsp 가 리뷰 EL 을 17곳 쓰기 때문에 빈 값을 넣어줘야 화면이 안 깨짐.
		 *   reviewStats 는 ${reviewStats.bestPercent} 처럼 5개 필드를 CSS width 에 쓰고 있어서
		 *   null 로 두면 "width: %" 가 되므로 0 으로 채운 Map 을 넣음.
		 *   담당자 Mapper 가 생기면 아래 4줄을 ProductService 호출로 바꾸면 됨.
		 */
		Map<String, Integer> emptyReviewStats = new LinkedHashMap<>();
		emptyReviewStats.put("bestPercent", 0);
		emptyReviewStats.put("goodPercent", 0);
		emptyReviewStats.put("normalPercent", 0);
		emptyReviewStats.put("poorPercent", 0);
		emptyReviewStats.put("badPercent", 0);

		model.addAttribute("reviews", new ArrayList<>());
		model.addAttribute("reviewStats", emptyReviewStats);
		model.addAttribute("reviewCount", 0);
		model.addAttribute("avgRating", 0.0);

		return "product.detail";
	}

	/**
	 * 옵션 변경 ajax — 구버전 ProductOptionServlet / ProductOptionHandler.
	 * product.js 가 옵션을 바꿀 때마다 GET 으로 부르고, 받은 값으로 가격·재고·사진을 다시 그림.
	 * Service 가 이미 JSON 문자열을 만들어주므로 그대로 내려보냄(@ResponseBody).
	 */
	@GetMapping(value = "/option", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String option(
			@RequestParam(value = "optionId", required = false) String optionIdParam,
			HttpServletResponse response) throws Exception {

		try {
			int optionId = Integer.parseInt(optionIdParam);

			ProductOptionDTO option = productOptionService.getOption(optionId);

			if (option == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "옵션을 찾을 수 없습니다.");
				return null;
			}

			List<ProductImageDTO> images = productOptionService.getImages(optionId);

			return productOptionService.buildOptionJson(option, images);

		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "optionId 가 올바르지 않습니다.");
			return null;
		}
	}



}
