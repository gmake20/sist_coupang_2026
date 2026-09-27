package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.PaymentMethodDTO;

public interface PaymentMethodMapper {

    // 계좌 목록
    List<PaymentMethodDTO> getBankAccounts(
            @Param("memberNo") Long memberNo
    );

    // 카드 목록
    List<PaymentMethodDTO> getCards(
            @Param("memberNo") Long memberNo
    );

    // 전체 결제수단
    List<PaymentMethodDTO> getPaymentMethods(
            @Param("memberNo") Long memberNo
    );

    // 해당 결제수단이 회원 소유인지 확인
    int existsPaymentMethod(
            @Param("memberNo") Long memberNo,
            @Param("paymentMethodNo") int paymentMethodNo
    );

    // 결제수단 단건 조회
    PaymentMethodDTO findPaymentMethod(
            @Param("memberNo") Long memberNo,
            @Param("paymentMethodNo") int paymentMethodNo
    );

    // 해당 타입의 기존 기본 결제수단 해제
    int clearDefault(
            @Param("memberNo") Long memberNo,
            @Param("paymentType") String paymentType
    );

    // 계좌 등록
    int insertBankAccount(
            PaymentMethodDTO dto
    );

    // 카드 등록
    int insertCard(
            PaymentMethodDTO dto
    );

    // 공통 결제수단 등록
    int insertPaymentMethod(
            PaymentMethodDTO dto
    );

    // 기본 결제수단 설정
    int setDefault(
            @Param("memberNo") Long memberNo,
            @Param("paymentMethodNo") int paymentMethodNo
    );
}