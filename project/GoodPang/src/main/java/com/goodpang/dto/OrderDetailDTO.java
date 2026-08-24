package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter

public class OrderDetailDTO {
    private int orderDetailNo; // ORDER_DETAIL_NO (PK)
    private int orderNo;        // ORDER_NO (FK - 주문번호)
    private int productNo;      // PRODUCT_NO (FK - 상품번호)
    private int orderQty;       // ORDER_QTY (주문수량)
    private int price;          // PRICE (주문당시 단가)
    private Integer optionId;   // OPTION_ID (NULL 허용이므로 Integer)
    
    // 화면(JSP) 출력을 위한 추가 변수 (JOIN 결과용)
    private String productName; // 상품 테이블에서 가져올 상품명
    private String optionName;  // 옵션 테이블에서 가져올 옵션명
    
   
}