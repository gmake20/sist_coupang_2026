package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.AddressDTO;

public interface AddressMapper {

	
	AddressDTO getAddress(
            @Param("memberNo") long memberNo
    );

    // 전체 배송지 조회
    List<AddressDTO> getAddressList(
            @Param("memberNo") long memberNo
    );
    
    int insertAddress(AddressDTO dto);

    int clearDefaultAddress(
            @Param("memberNo") Long memberNo
    );
    
    int resetDefaultAddress(
            @Param("memberNo") Long memberNo,
            @Param("addressNo") Integer addressNo
    );
    
    AddressDTO getAddressForEdit(
            @Param("addressNo") Integer addressNo,
            @Param("memberNo") Long memberNo
    );

    int updateAddress(AddressDTO dto);
    
    AddressDTO editGetAddress(
            @Param("addressNo") int addressNo,
            @Param("memberNo") Long memberNo
    );
}