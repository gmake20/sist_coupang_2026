package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;

public interface WowMembershipMapper {

    int isWowMember(
            @Param("memberNo") Long memberNo
    );
}