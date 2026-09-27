package org.doit.goodpang.service;

import java.util.Date;

import org.doit.goodpang.domain.WowMembershipDTO;
import org.doit.goodpang.mapper.WowMembershipMapper;
import org.doit.goodpang.mapper.WowPaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
@RequiredArgsConstructor
public class WowMembershipService {
	
	 private static final int WOW_PRICE = 7890;

    private final WowMembershipMapper wowMembershipMapper;
    private final WowPaymentMapper wowPaymentMapper;


    // 현재 와우 회원인지 확인
    public boolean isWowMember(Long memberNo) {

        return wowMembershipMapper
                .isWowMember(memberNo) > 0;
    }


    // 회원 탈퇴 차단 여부
    public boolean isWithdrawalBlocked(
            Long memberNo) {

        return wowMembershipMapper
                .isWithdrawalBlocked(memberNo) > 0;
    }


    // 다음 결제일
    public Date nextPaymentDate(
            Long memberNo) {

        return wowMembershipMapper
                .nextPaymentDate(memberNo);
    }


    // 해지 예정인지 확인
    public boolean isCancelPending(
            Long memberNo) {

        return wowMembershipMapper
                .isCancelPending(memberNo) > 0;
    }


    // 회원 멤버십 조회
    public WowMembershipDTO findByMemberNo(
            Long memberNo) {

        return wowMembershipMapper
                .findByMemberNo(memberNo);
    }


    // 와우 멤버십 신규 가입
    @Transactional
    public int insertMembership(
            Long memberNo,
            int paymentMethodNo) {

        // 시퀀스 번호 미리 생성
        int wowMembershipNo =
                wowMembershipMapper
                        .getNextMembershipNo();

        int result =
                wowMembershipMapper
                        .insertMembership(
                                wowMembershipNo,
                                memberNo,
                                paymentMethodNo
                        );

        if (result <= 0) {
            throw new RuntimeException(
                    "와우 멤버십 등록에 실패했습니다."
            );
        }

        return wowMembershipNo;
    }


    // 와우 멤버십 해지
    @Transactional
    public int cancelMembership(
            Long memberNo) {

        return wowMembershipMapper
                .cancelMembership(memberNo);
    }


    // 와우 멤버십 재활성화
    @Transactional
    public int reactivateMembership(
            Long memberNo,
            int paymentMethodNo) {

        return wowMembershipMapper
                .reactivateMembership(
                        memberNo,
                        paymentMethodNo
                );
    }
    
    public WowMembershipDTO getMembershipDetail(
            Long memberNo) {

        return wowMembershipMapper
                .getMembershipDetail(memberNo);
    }

    @Transactional
    public int joinMembership(
            Long memberNo,
            int paymentMethodNo) {

        WowMembershipDTO membership =
                wowMembershipMapper
                        .findByMemberNo(memberNo);


        int wowMembershipNo;

        if (membership == null) {

            wowMembershipNo =
                    wowMembershipMapper
                            .getNextMembershipNo();


            int insertResult =
                    wowMembershipMapper
                            .insertMembership(
                                    wowMembershipNo,
                                    memberNo,
                                    paymentMethodNo
                            );


            if (insertResult != 1) {

                throw new IllegalStateException(
                        "와우 멤버십 등록 실패"
                );
            }

        }

        else {

            wowMembershipNo =
                    membership.getWowMembershipNo();


            int updateResult =
                    wowMembershipMapper
                            .reactivateMembership(
                                    memberNo,
                                    paymentMethodNo
                            );


            if (updateResult != 1) {

                throw new IllegalStateException(
                        "와우 멤버십 재가입 처리 실패"
                );
            }
        }

        int paymentResult =
                wowPaymentMapper.insertPayment(
                        wowMembershipNo,
                        memberNo,
                        paymentMethodNo,
                        WOW_PRICE,
                        "JOIN",
                        "SUCCESS"
                );


        if (paymentResult != 1) {

            throw new IllegalStateException(
                    "와우 멤버십 결제내역 저장 실패"
            );
        }


        return wowMembershipNo;
    }
    
    
}