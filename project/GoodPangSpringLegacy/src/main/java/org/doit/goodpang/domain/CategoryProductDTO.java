package org.doit.goodpang.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 카테고리 목록 페이지(category_list.jsp) 카드 한 장에 필요한 값만 담는 DTO.
 * ProductDTO(상세페이지용, 설명·판매자 정보까지)와도 다르고, CategoryDTO(카테고리 트리 자체)와도 다름.
 *
 * 가격은 이미 "PRODUCT_PRICE + 옵션 추가금" 계산이 끝난 총액으로 채워서 내려줌(2026-08-30 팀 확정,
 * ref/category/STRUCTURE.md 11장 참고) — JSP 에서 다시 계산하지 않음.
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryProductDTO {

    private int productNo;
    private String productName;
    private String thumbnailUrl;       // PRODUCT_IMAGE.IMAGE_URL — contextPath 는 JSP 에서 붙임. null 이면 사진 없음

    /*
     * 이 카드가 가리키는 대표 옵션(2026-09-06 추가).
     * 색상 필터를 걸면 "그 색상 옵션 중 첫 번째", 안 걸면 예전처럼 "상품의 첫 옵션".
     * 카드 링크에 붙여서 상세페이지가 같은 옵션을 선택된 상태로 열게 함(원본 쿠팡도 itemId 를 이렇게 넘김).
     * 옵션이 하나도 없는 상품이면 0 → JSP 에서 링크에 안 붙임.
     */
    private int optionId;

    private int salePrice;             // PRODUCT_PRICE + 최저가 옵션의 PRICE
    private Integer normalPrice;       // PRODUCT_PRICE + 최저가 옵션의 NORMAL_PRICE. 정상가가 판매가보다 클 때만 값이 들어감(그 외 null)
   // private int discountRate;          // normalPrice 가 있을 때만 0보다 큼 -> getDiscountRate()에서 계산

    private double avgRating;          // 리뷰 없으면 0
    private int reviewCount;           // 리뷰 없으면 0
    private int saleCount;             // ORDER_DETAIL.ORDER_QTY 합계(주문취소 제외). 판매량순 정렬 기준

   // private int cashReward;            // 적립 -> getCashReward()에서 계산 
    private boolean soldOut;
    

    /*
     * 할인율 — 구버전 CategoryProductDAO.mapRow() 에서 계산하던 것을 옮김.
     * MyBatis 는 DB 컬럼만 채우므로, 계산값은 꺼낼 때(getter) 계산함.
     * 정상가가 판매가보다 클 때만 0보다 큼
     */
    public int getDiscountRate( ) {
    	if (normalPrice == null || normalPrice <= salePrice) return 0; 
		return (int) Math.round((1 - (double) salePrice / normalPrice) * 100);	
    }//getDiscountRate

    // 적립금 — 판매가의 5% (구버전 mapRow() 그대로)
    public int getCashReward() {
    	return (int) Math.floor(salePrice * 0.05); 
    }//getCashReward
    
}
