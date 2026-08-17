package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private String name;
    private String roadAddress;
    private String detailAddress;
    private String phone;
    private boolean defaultAddress;
}