package org.doit.goodpang.domain;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class MemberVO {
	// member 테이블 컬럼명 필드명 동일 확인
	private String id; // 수정
	private String pwd;
	private String name;
	private String gender;
	
	private String birth;
	/*
	  <input type="date" name="birth" id="birth">s
	  @DateTimeFormat(pattern = "yyyy-MM-dd") 
	  private Date birth;
	 */
	private String is_lunar; // 수정
	private String cphone;  // 수정
	private String email;
	private String habit;
	private Date   regdate; // 수정
	
	private int point; // 추가
	
	// 
	private boolean enabled;
	
	// 회원이 소유한 권한들 저장
	private List<AuthVO> authList;
	
}
