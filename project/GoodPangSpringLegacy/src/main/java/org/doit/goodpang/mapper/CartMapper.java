package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;

public interface CartMapper {
	
	 int clearCart(
	        @Param("memberNo") Long memberNo
	    );

}
