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


    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getPaymentMethods(
            long memberNo) {

        return paymentMethodMapper
                .getPaymentMethods(memberNo);
    }


    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getBankMethods(
            long memberNo) {

        return paymentMethodMapper
                .getBankMethods(memberNo);
    }


    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getCardMethods(
            long memberNo) {

        return paymentMethodMapper
                .getCardMethods(memberNo);
    }
}