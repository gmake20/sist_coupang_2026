package org.doit.goodpang.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.SellerDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMapper {
	
	public SellerDTO findByEmail(@Param("email") String email) throws ClassNotFoundException, SQLException;;

	// 판매자 정보관리 제출 - 사업장 주소/통신판매업번호/대표카테고리/정산계좌/서류 경로. 입점 대기·반려면 '심사 중'으로 전환
	public int updateBusinessInfo(SellerDTO dto);

	// ===== 판매자 입점 신청(회원가입) =====

	// 같은 이메일로 가입된 판매자 수 (0이면 사용 가능)
	public int countByEmail(@Param("email") String email);

	// 같은 사업자등록번호로 입점 신청된 판매자 수 (0이면 사용 가능)
	public int countByBusinessNo(@Param("businessNo") String businessNo);

	// 입점 신청 - '입점 대기' 상태로 SELLER 한 행 추가 (sellerPw는 BCrypt 해시)
	public int insertSeller(SellerDTO dto);
	/*
	 * public MemberVO getMember(@Param("id") String id) throws
	 * ClassNotFoundException, SQLException;
	 * 
	 * public int insert(MemberVO member) throws ClassNotFoundException,
	 * SQLException;
	 * 
	 * // 회원 ID(username)를 매개변수로 회원 정보를 반환하는 메서드 / UserDetailsService implement한 구현체
	 * 안에 메서드 public MemberVO read(@Param("userid") String userid) throws
	 * ClassNotFoundException, SQLException;
	 */

	// 판매자 상태 변경 ('탈퇴', 관리자 승인/반려/정지 등). rejectReason은 null 가능
	public int updateApprovalStatus(@Param("sellerNo") int sellerNo, @Param("approvalStatus") String approvalStatus,
			@Param("rejectReason") String rejectReason);

	// ===== 관리자 판매자 입점 심사 (비밀번호 해시는 조회하지 않음) =====

	// 전체 판매자 - 최근 가입순
	public List<SellerDTO> findAllSellers();

	// 판매자 1명. 없으면 null
	public SellerDTO findBySellerNo(@Param("sellerNo") int sellerNo);

}
