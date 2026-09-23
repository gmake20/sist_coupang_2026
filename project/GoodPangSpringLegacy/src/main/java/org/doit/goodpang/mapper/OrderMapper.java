package org.doit.goodpang.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.AddressDTO;
import org.doit.goodpang.domain.OrderCompleteDTO;
import org.doit.goodpang.domain.OrderItemDTO;
import org.doit.goodpang.service.OrderService.StockFail;

public interface OrderMapper {
	
	 int getOrderCount(@Param("memberNo")long memberNo, @Param("yearFilter") String yearFilter);
	    List<OrderItemDTO> getOrderListPaged(long memberNo, String yearFilter, int page, int pageSize);
	    List<OrderItemDTO> getOrderDetailList(int orderNo, long memberNo);


    Integer findOrderNoByCheckout(
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo
    );

    int countCheckout(
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo
    );

    int countAddress(
            @Param("addressNo") int addressNo,
            @Param("memberNo") Long memberNo
    );

    int insertOrderAddress(
            @Param("addressNo") int addressNo,
            @Param("memberNo") Long memberNo
    );

    Integer getLastOrderAddressNo();

    int insertOrderFromCheckout(
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo,
            @Param("orderAddressNo") int orderAddressNo,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentMethodNo") Integer paymentMethodNo
    );

    Integer getLastOrderNo();

    int insertOrderDetailsFromCheckout(
            @Param("checkoutNo") int checkoutNo,
            @Param("orderNo") int orderNo
    );

    StockFail stockOut(
            @Param("orderNo") int orderNo
    );

    int deleteCheckoutItems(
            @Param("checkoutNo") int checkoutNo
    );

    int deleteCheckout(
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo
    );
    
    AddressDTO getAddress(
            @Param("memberNo") Long memberNo
    );

    List<AddressDTO> getAddressList(
            @Param("memberNo") Long memberNo
    );

   


    int existsCheckout(
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo
    );

    int existsAddress(
            @Param("addressNo") int addressNo,
            @Param("memberNo") Long memberNo
    );

    int getNextOrderAddressNo();

    int insertOrderAddress(
            @Param("orderAddressNo") int orderAddressNo,
            @Param("addressNo") int addressNo,
            @Param("memberNo") Long memberNo
    );

    int getNextOrderNo();

    int insertOrderFromCheckout(
            @Param("orderNo") int orderNo,
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo,
            @Param("orderAddressNo") int orderAddressNo
    );


    int insertPayment(
            @Param("orderNo") int orderNo,
            @Param("checkoutNo") int checkoutNo,
            @Param("memberNo") Long memberNo,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentMethodNo") Integer paymentMethodNo
    );



    void stockOut(
            Map<String, Object> params
    );

    int updateTotalPrice(
            @Param("orderNo") int orderNo
    );

    int updateOrderStatus(
            @Param("orderNo") int orderNo,
            @Param("orderStatus") String orderStatus
    );

    int updateOrderAddress(
            @Param("orderNo") int orderNo,
            @Param("addressNo") int addressNo
    );
    
    OrderCompleteDTO getOrderComplete(
            @Param("orderNo") int orderNo,
            @Param("memberNo") Long memberNo
    );
}