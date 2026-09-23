package org.doit.goodpang.service;

import java.util.List;

import org.doit.goodpang.domain.AddressDTO;
import org.doit.goodpang.mapper.AddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;


    @Transactional(readOnly = true)
    public AddressDTO getAddress(long memberNo) {

        return addressMapper.getAddress(memberNo);
    }


    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressList(long memberNo) {

        return addressMapper.getAddressList(memberNo);
    }


    @Transactional
    public int insertAddress(AddressDTO dto) {

        if ("Y".equals(dto.getAddressDefault())) {

            addressMapper.clearDefaultAddress(
                    dto.getMemberNo()
            );
        }

        return addressMapper.insertAddress(dto);
    }
}