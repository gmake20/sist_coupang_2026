package com.goodpang.dto;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WowMembershipDTO {

    private int wowMembershipNo;
    private int memberNo;

    private String status;

    private Date startDate;
    private Date nextPaymentDate;
    private Date cancelDate;
    private Date endDate;

    private String autoPaymentYn;

    private Integer paymentMethodNo;
}