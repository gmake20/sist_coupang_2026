package org.doit.ik.domain;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItemVO {

	private int orderNo; 
	private int memberNo;
	private String orderStatus; 
	/* private LocalDate orderDate; */
	private Timestamp orderDate;
	private int totalPrice;
	private int itemPrice;
	private String productName;
	private int quantity;
	private String option1Type;
	private String option1Value;
	private String option2Type;
	private String option2Value;
	private Long orderDetailNo;
    private Long productNo;
    private int deliveryFee;       // 배송비
    //이미지
    private String imageUrl;

}