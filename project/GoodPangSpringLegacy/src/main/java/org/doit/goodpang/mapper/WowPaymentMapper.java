package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;

public interface WowPaymentMapper {

	int insertPayment(
            @Param("wowMembershipNo") int wowMembershipNo,
            @Param("memberNo") long memberNo,
            @Param("paymentMethodNo") int paymentMethodNo,
            @Param("amount") int amount,
            @Param("paymentType") String paymentType,
            @Param("paymentStatus") String paymentStatus
    );

    int cancelMembership(
            @Param("memberNo") int memberNo
    );
}