package org.doit.goodpang.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDTO {

    private int paymentMethodNo;
    private long memberNo;

    private String paymentType;

    private String bankCode;
    private String bankName;

    private String accountLast4;
    private String accountHolder;

    private boolean paymentDefault;
    
    private String cardCompany;
    private String cardLast4;
}