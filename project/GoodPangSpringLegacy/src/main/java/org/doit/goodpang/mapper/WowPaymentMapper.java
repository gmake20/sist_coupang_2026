package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;

public interface WowPaymentMapper {

    int insertPayment(
            @Param("wowMembershipNo")
            int wowMembershipNo,

            @Param("memberNo")
            Long memberNo,

            @Param("paymentMethodNo")
            int paymentMethodNo,

            @Param("amount")
            int amount,

            @Param("paymentType")
            String paymentType,

            @Param("status")
            String status
    );
}