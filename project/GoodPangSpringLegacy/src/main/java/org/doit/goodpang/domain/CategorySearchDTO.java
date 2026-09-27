package org.doit.goodpang.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 카테고리 목록(PLP) 검색 조건 — 교재의 Criteria 와 같은 역할.
 *
 * 필드명 = 주소 파라미터명 → Controller 에서 스프링이 자동으로 채워줌
 *   /category/103?sort=PRICE_ASC&page=2&listSize=120&minPrice=10000&rating=4&color=Black&color=White
 *   categoryNo 는 경로(/category/{categoryNo})에서 채워짐
 *
 * 주소에 없는 파라미터는 setter 가 안 불려서 아래 초기값이 그대로 남음 (= 기본값)
 * categoryNo 는 기본값 없음 — 잘못된 번호는 Controller 가 메인으로 리다이렉트
 *
 * 구버전 CategoryHandler 에서 parseIntOrDefault 로 하나씩 꺼내던 것들을 이 클래스 하나로 모음.
 * 같은 객체를 countByCategory / findByCategory 에 그대로 넘김 (mapper.xml 에서 #{필드명} 으로 꺼냄)
 */
@Getter
@Setter
public class CategorySearchDTO {
	
	private int categoryNo; 			// 경로 /category/{categoryNo}
	private String sort = "LATEST"; 	// 정렬 5종 : 아래 normalize()
	private int page = 1;				// 현재 페이지(1부터)
	private int listSize = 60;			// 목록 보기 갯수 60개
	private int minPrice = 0;			// 가격 필터 : 안고르면 전체
	private int maxPrice = Integer.MAX_VALUE;
	private int rating = 0;				// 평점 N점 이상 (0=전체)
	private List<String> color;			// 색상 다중 선택
	
	public void normalize() {
		// 정렬 화이트리스트 : 5개 외에는 최신순 (mapper.xml의 choose와 같은 목록)
		if (!List.of("LATEST", "PRICE_ASC", "PRICE_DESC", "SALE_COUNT", "RANKING").contains(sort)) {
			sort = "LATEST";
		}// if List
		page = Math.max(1, page);
		if(listSize !=60 && listSize != 120) listSize = 60;
		minPrice = Math.max(0, minPrice);
		if(maxPrice < minPrice) maxPrice = Integer.MAX_VALUE;
		rating = Math.max(0, Math.min(5, rating));
		if(color != null) color.removeIf(c -> c == null || c.isBlank());
			
	}// normalize
	
	 /** 몇 개 건너뛸지 — mapper.xml 에서 #{offset} 으로 씀 (MyBatis 가 getOffset() 을 부름) */
	public int getOffset() {
		return (page - 1) * listSize;
	} // getOffset
	
	
} // class
