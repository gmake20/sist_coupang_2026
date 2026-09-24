package org.doit.goodpang.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.WowMembershipDTO;

public interface WowMembershipMapper {

    int isWowMember(
            @Param("memberNo") Long memberNo
    );
    
    WowMembershipDTO getMembershipDetail(
            @Param("memberNo")
            Long memberNo
    );
    // 회원 탈퇴 차단 여부
    int isWithdrawalBlocked(
            @Param("memberNo") Long memberNo
    );

    // 다음 결제일
    Date nextPaymentDate(
            @Param("memberNo") Long memberNo
    );

    // 해지 예정 상태인지 확인
    int isCancelPending(
            @Param("memberNo") Long memberNo
    );

    // 회원의 와우 멤버십 조회
    WowMembershipDTO findByMemberNo(
            @Param("memberNo") Long memberNo
    );

    // 다음 멤버십 번호
    int getNextMembershipNo();

    // 신규 멤버십 등록
    int insertMembership(
            @Param("wowMembershipNo") int wowMembershipNo,
            @Param("memberNo") Long memberNo,
            @Param("paymentMethodNo") int paymentMethodNo
    );

    // 멤버십 해지
    int cancelMembership(
            @Param("memberNo") Long memberNo
    );

    // 멤버십 재활성화
    int reactivateMembership(
            @Param("memberNo") Long memberNo,
            @Param("paymentMethodNo") int paymentMethodNo
    );
    
}