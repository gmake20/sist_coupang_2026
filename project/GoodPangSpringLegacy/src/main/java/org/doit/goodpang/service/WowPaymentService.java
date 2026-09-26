package org.doit.goodpang.service;

import org.doit.goodpang.mapper.WowPaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WowPaymentService {

    private final WowPaymentMapper wowPaymentMapper;


    @Transactional
    public int insertPayment(
            int wowMembershipNo,
            int memberNo,
            int paymentMethodNo,
            int amount,
            String paymentType,
            String paymentStatus) {

        return wowPaymentMapper.insertPayment(
                wowMembershipNo,
                memberNo,
                paymentMethodNo,
                amount,
                paymentType,
                paymentStatus
        );
    }


    @Transactional
    public void cancelMembership(int memberNo) {

        int result =
                wowPaymentMapper.cancelMembership(memberNo);

        if (result == 0) {
            throw new IllegalStateException(
                    "해지할 활성 와우 멤버십이 없습니다."
            );
        }
    }
}