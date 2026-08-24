package com.goodpang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
	private int orderNo; // <-- 추가
    private String productName;
    private String optionName;
    private int salePrice;
    private int quantity;
    private boolean freeDelivery;
}