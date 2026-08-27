package com.goodpang.dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter

public class OrderDetailDTO {

	private int orderNo; 
	private int memberNo;
	private String orderStatus; 
	/* private LocalDate orderDate; */
	private Timestamp orderDate;
	private int totalPrice; 
	private String productName; 
	private int quantity;
	private String option1Type;
	private String option1Value;
	private String option2Type;
	private String option2Value;
	private Long orderDetailNo;
    private Long productNo;
    private int deliveryFee;
    private String paymentMethod;
    private String requestMsg;
    private String address;
    private String detailAddress;
    private String memberName;
    private String phone;
    
    
    // 포맷 관련 필드 추가
    private String cardCompanyName;
    private String bankName;
    
}