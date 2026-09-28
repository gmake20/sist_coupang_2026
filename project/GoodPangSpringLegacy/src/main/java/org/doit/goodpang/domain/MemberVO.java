package org.doit.goodpang.domain;

import java.io.Serializable;
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
public class MemberVO implements Serializable{
	
      private static final long serialVersionUID = 1L;
	
	  private Long memberNo;
      private String memberId;
      private String memberPw;
      private String memberName;
      private String phone;
	  private String email;
	  private String rank;
	  private int status;
}
