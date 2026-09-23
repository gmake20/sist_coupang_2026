package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;

public interface CheckoutMapper {
	
	 Integer getProductPrice(
	            @Param("productNo") int productNo
	    );


	    Integer getOptionPrice(
	            @Param("productNo") int productNo,
	            @Param("optionId") int optionId
	    );


	    int getNextCheckoutNo();


	    int insertCheckout(
	            @Param("checkoutNo") int checkoutNo,
	            @Param("memberNo") Long memberNo,
	            @Param("productAmount") int productAmount,
	            @Param("instantDiscount") int instantDiscount,
	            @Param("couponDiscount") int couponDiscount,
	            @Param("cashUsed") int cashUsed,
	            @Param("deliveryFee") int deliveryFee,
	            @Param("totalPrice") int totalPrice
	    );


	    int insertCheckoutItem(
	            @Param("checkoutNo") int checkoutNo,
	            @Param("productNo") int productNo,
	            @Param("optionId") Integer optionId,
	            @Param("quantity") int quantity,
	            @Param("unitPrice") int unitPrice
	    );

}
