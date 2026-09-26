package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.CheckoutDTO;
import org.doit.goodpang.domain.CheckoutItemDTO;

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
	            @Param("memberNo") long memberNo,
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
	    
	    CheckoutDTO getCheckout(
	            @Param("checkoutNo") int checkoutNo,
	            @Param("memberNo") long memberNo
	    );
	    
	    CheckoutDTO getCheckout(
	            @Param("checkoutNo") int checkoutNo,
	            @Param("memberNo") Long memberNo
	    );

	    List<CheckoutItemDTO> getCheckoutItems(
	            @Param("checkoutNo") int checkoutNo
	    );

	    List<CheckoutItemDTO> getCheckoutItemsProduct(
	            @Param("checkoutNo") int checkoutNo
	    );

	    int createCheckout(
	            CheckoutDTO checkout
	    );

	    int addCheckoutItem(
	            @Param("checkoutNo") int checkoutNo,
	            @Param("optionId") int optionId,
	            @Param("quantity") int quantity
	    );

	    int updateCheckoutAmount(
	            @Param("checkoutNo") int checkoutNo
	    );

	    int updateCheckoutAmountV2(
	            @Param("checkoutNo") int checkoutNo
	    );


}
