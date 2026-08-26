package com.goodpang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * PRODUCT + SELLER + SUB_CATEGORY/MID_CATEGORY/MAIN_CATEGORY 조인 결과 한 행.
 * 실제 PRODUCT 테이블엔 할인율/원가/평점/리뷰수/사진 컬럼이 없음 —
 * 그건 스키마 검토(스키마_검토.md)에서 "나중에"로 보류된 부분이라 여기 안 넣음.
 * product.jsp 에서 그 값들은 당분간 계속 하드코딩으로 둘 것.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private int productNo;
    private String productName;
    private String productDesc;
    private int productPrice;
    private int quantity;

    private int sellerNo;
    private String storeName;          // SELLER.STORE_NAME — product.jsp 의 .brand-info 자리
    private String ceoName;               // SELLER.CEO_NAME
    private String businessAddress;       // SELLER.BUSINESS_ADDRESS
    private String businessDetailAddress; // SELLER.BUSINESS_DETAIL_ADDRESS
    private String email;                 // SELLER.EMAIL
    private String phone;                 // SELLER.PHONE
    private String mailOrderNo;           // SELLER.MAIL_ORDER_NO
    private String businessNo;            // SELLER.BUSINESS_NO

    private int subCategoryNo;
    private String subCategoryName;    // 빵부스러기 맨 끝 칸
    private String midCategoryName;
    private String mainCategoryName;   // 빵부스러기 맨 앞 칸

    
    
}
