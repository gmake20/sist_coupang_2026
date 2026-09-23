package org.doit.goodpang.service;

import java.util.HashMap;
import java.util.Map;

import org.doit.goodpang.domain.OrderCompleteDTO;
import org.doit.goodpang.mapper.CartMapper;
import org.doit.goodpang.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;

    @Transactional
    public OrderResult createOrder(
            int checkoutNo,
            Long memberNo,
            int addressNo,
            String paymentMethod,
            Integer paymentMethodNo) {

        Integer existingOrderNo =
                orderMapper.findOrderNoByCheckout(
                        checkoutNo,
                        memberNo
                );

        if (existingOrderNo != null) {

            return OrderResult.alreadyCompleted(
                    existingOrderNo
            );
        }

        int checkoutCount =
                orderMapper.existsCheckout(
                        checkoutNo,
                        memberNo
                );

        if (checkoutCount == 0) {

            throw new IllegalStateException(
                    "존재하지 않거나 접근할 수 없는 checkout입니다."
            );
        }

        int addressCount =
                orderMapper.existsAddress(
                        addressNo,
                        memberNo
                );

        if (addressCount == 0) {

            throw new IllegalArgumentException(
                    "잘못된 배송지입니다."
            );
        }


        int orderAddressNo =
                orderMapper.getNextOrderAddressNo();


        int addressInsertCount =
                orderMapper.insertOrderAddress(
                        orderAddressNo,
                        addressNo,
                        memberNo
                );

        if (addressInsertCount != 1) {

            throw new IllegalStateException(
                    "ORDER_ADDRESS 저장 실패"
            );
        }


        int orderNo =
                orderMapper.getNextOrderNo();


        int orderInsertCount =
                orderMapper.insertOrderFromCheckout(
                        orderNo,
                        checkoutNo,
                        memberNo,
                        orderAddressNo
                );

        if (orderInsertCount != 1) {

            throw new IllegalStateException(
                    "ORDERS 생성 실패"
            );
        }

        int paymentInsertCount =
                orderMapper.insertPayment(
                        orderNo,
                        checkoutNo,
                        memberNo,
                        paymentMethod,
                        paymentMethodNo
                );

        if (paymentInsertCount != 1) {

            throw new IllegalStateException(
                    "PAYMENT 생성 실패"
            );
        }


        int detailCount =
                orderMapper.insertOrderDetailsFromCheckout(
                        checkoutNo,
                        orderNo
                );

        if (detailCount <= 0) {

            throw new IllegalStateException(
                    "주문 상품 저장 실패"
            );
        }


        Map<String, Object> params =
                new HashMap<>();

        params.put(
                "orderNo",
                orderNo
        );

        orderMapper.stockOut(params);

        Number resultNumber =
                (Number) params.get("result");

        int result =
                resultNumber == null
                ? 0
                : resultNumber.intValue();

        if (result != 0) {

            String failProduct =
                    (String) params.get(
                            "failProduct"
                    );

            Number failLeftNumber =
                    (Number) params.get(
                            "failLeft"
                    );

            int failLeft =
                    failLeftNumber == null
                    ? 0
                    : failLeftNumber.intValue();


            StockFail stockFail =
                    new StockFail(
                            failProduct,
                            failLeft
                    );

            throw new StockOutException(
                    stockFail
            );
        }

        orderMapper.deleteCheckoutItems(
                checkoutNo
        );

        int checkoutDeleteCount =
                orderMapper.deleteCheckout(
                        checkoutNo,
                        memberNo
                );

        if (checkoutDeleteCount != 1) {

            throw new IllegalStateException(
                    "CHECKOUT 삭제 실패"
            );
        }

        return OrderResult.success(
                orderNo
        );
    }

    @Transactional(readOnly = true)
    public OrderCompleteDTO getOrderComplete(
            int orderNo,
            Long memberNo) {

        return orderMapper.getOrderComplete(
                orderNo,
                memberNo
        );
    }

    @Getter
    public static class OrderResult {

        private Integer orderNo;

        private boolean alreadyCompleted;


        private OrderResult() {
        }


        public static OrderResult success(
                Integer orderNo) {

            OrderResult result =
                    new OrderResult();

            result.orderNo =
                    orderNo;

            return result;
        }


        public static OrderResult alreadyCompleted(
                Integer orderNo) {

            OrderResult result =
                    new OrderResult();

            result.orderNo =
                    orderNo;

            result.alreadyCompleted =
                    true;

            return result;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class StockFail {

        private String productName;

        private int left;
    }

    @Getter
    public static class StockOutException
            extends RuntimeException {

        private static final long serialVersionUID =
                1L;

        private final StockFail stockFail;


        public StockOutException(
                StockFail stockFail) {

            super("재고가 부족합니다.");

            this.stockFail =
                    stockFail;
        }
    }
}