package org.doit.goodpang.service;

import java.util.List;

import org.doit.goodpang.domain.PaymentMethodDTO;
import org.doit.goodpang.mapper.PaymentMethodMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodMapper paymentMethodMapper;


    // 계좌 목록
    public List<PaymentMethodDTO> getBankMethods(
            Long memberNo) {

        return paymentMethodMapper
                .getBankAccounts(memberNo);
    }


    // 카드 목록
    public List<PaymentMethodDTO> getCardMethods(
            Long memberNo) {

        return paymentMethodMapper
                .getCards(memberNo);
    }

    public List<PaymentMethodDTO> getPaymentMethods(
            Long memberNo) {

        return paymentMethodMapper
                .getPaymentMethods(memberNo);
    }

    public boolean existsPaymentMethod(
            Long memberNo,
            int paymentMethodNo) {

        return paymentMethodMapper
                .existsPaymentMethod(
                        memberNo,
                        paymentMethodNo
                ) > 0;
    }


    // 결제수단 단건 조회
    public PaymentMethodDTO findPaymentMethod(
            Long memberNo,
            int paymentMethodNo) {

        return paymentMethodMapper
                .findPaymentMethod(
                        memberNo,
                        paymentMethodNo
                );
    }

    @Transactional
    public int insertBankAccount(
            PaymentMethodDTO dto) {

        log.info(
                "계좌 등록 memberNo : "
                + dto.getMemberNo()
        );


        if (dto.isPaymentDefault()) {

            paymentMethodMapper.clearDefault(
                    Long.valueOf(dto.getMemberNo()),
                    "BANK"
            );
        }


        return paymentMethodMapper
                .insertBankAccount(dto);
    }

    @Transactional
    public int insertCard(
            PaymentMethodDTO dto) {

        log.info(
                "카드 등록 memberNo : "
                + dto.getMemberNo()
        );


        if (dto.isPaymentDefault()) {

            paymentMethodMapper.clearDefault(
                    Long.valueOf(dto.getMemberNo()),
                    "CARD"
            );
        }


        return paymentMethodMapper
                .insertCard(dto);
    }

    @Transactional
    public int insertPaymentMethod(
            PaymentMethodDTO dto) {

        if (dto.isPaymentDefault()) {

            paymentMethodMapper.clearDefault(
                    Long.valueOf(dto.getMemberNo()),
                    dto.getPaymentType()
            );
        }


        return paymentMethodMapper
                .insertPaymentMethod(dto);
    }

    @Transactional
    public int setDefault(
            Long memberNo,
            int paymentMethodNo) {


        // 해당 회원의 결제수단 조회
        PaymentMethodDTO paymentMethod =
                paymentMethodMapper
                        .findPaymentMethod(
                                memberNo,
                                paymentMethodNo
                        );


        if (paymentMethod == null) {

            throw new IllegalArgumentException(
                    "결제수단을 찾을 수 없습니다."
            );
        }


        // 같은 타입의 기존 기본값 해제
        paymentMethodMapper.clearDefault(
                memberNo,
                paymentMethod.getPaymentType()
        );


        // 새 기본 결제수단 설정
        return paymentMethodMapper.setDefault(
                memberNo,
                paymentMethodNo
        );
    }
}