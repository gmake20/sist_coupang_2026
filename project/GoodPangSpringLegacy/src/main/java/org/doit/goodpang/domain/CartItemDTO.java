package org.doit.goodpang.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private int memberNo;
    private int optionId;
    private int productNo;

    private String productName;

    private String option1Type;
    private String option1Value;
    private String option2Type;
    private String option2Value;
    private String option3Type;
    private String option3Value;

    private int productPrice;
    private int optionPrice;
    private int quantity;
    
    private String imageUrl;
    
    private int unitPrice;
    
    public int getTotalPrice() {
        return getUnitPrice() * quantity;
    }
    
    public int getUnitPrice() {
        return productPrice + optionPrice;
    }
    
    
}
