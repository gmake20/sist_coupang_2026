package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.PaymentMethodDTO;

public interface PaymentMethodMapper {

    // 전체 결제수단
    List<PaymentMethodDTO> getPaymentMethods(
            @Param("memberNo") long memberNo
    );

    // 계좌
    List<PaymentMethodDTO> getBankMethods(
            @Param("memberNo") long memberNo
    );

    // 카드
    List<PaymentMethodDTO> getCardMethods(
            @Param("memberNo") long memberNo
    );
}