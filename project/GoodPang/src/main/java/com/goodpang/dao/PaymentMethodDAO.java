package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.ConnectionProvider;

public class PaymentMethodDAO {

    /*
     * 회원 계좌 목록
     */
    public List<PaymentMethodDTO> getBankAccounts(
            int memberNo) {

        List<PaymentMethodDTO> list =
                new ArrayList<>();

        String sql = """
                SELECT
                    PAYMENT_METHOD_NO,
                    MEMBER_NO,
                    PAYMENT_TYPE,
                    BANK_CODE,

                    CASE BANK_CODE
                        WHEN 'SHINHAN' THEN '신한은행'
                        WHEN 'KB'     THEN 'KB국민은행'
                        WHEN 'WOORI'  THEN '우리은행'
                        WHEN 'NH'     THEN 'NH농협은행'
                        WHEN 'HANA'   THEN '하나은행'
                        WHEN 'KAKAO'  THEN '카카오뱅크'
                        WHEN 'TOSS'   THEN '토스뱅크'
                        ELSE BANK_CODE
                    END AS BANK_NAME,

                    ACCOUNT_LAST4,
                    ACCOUNT_HOLDER,
                    PAYMENT_DEFAULT

                FROM PAYMENT_METHOD

                WHERE MEMBER_NO = ?
                  AND PAYMENT_TYPE = 'BANK_TRANSFER'

                ORDER BY
                    CASE
                        WHEN PAYMENT_DEFAULT = 'Y'
                        THEN 0
                        ELSE 1
                    END,
                    CREATED_AT DESC
                """;

        try (
            Connection conn =
                ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs =
                     pstmt.executeQuery()) {

                while (rs.next()) {

                    PaymentMethodDTO dto =
                            new PaymentMethodDTO();

                    dto.setPaymentMethodNo(
                            rs.getInt(
                                "PAYMENT_METHOD_NO"
                            )
                    );

                    dto.setMemberNo(
                            rs.getInt("MEMBER_NO")
                    );

                    dto.setPaymentType(
                            rs.getString("PAYMENT_TYPE")
                    );

                    dto.setBankCode(
                            rs.getString("BANK_CODE")
                    );

                    dto.setBankName(
                            rs.getString("BANK_NAME")
                    );

                    dto.setAccountLast4(
                            rs.getString("ACCOUNT_LAST4")
                    );

                    dto.setAccountHolder(
                            rs.getString("ACCOUNT_HOLDER")
                    );

                    dto.setPaymentDefault(
                            "Y".equals(
                                rs.getString(
                                    "PAYMENT_DEFAULT"
                                )
                            )
                    );

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /*
     * 기존 기본 결제수단 해제
     */
    public int clearDefault(
            Connection conn,
            int memberNo)
            throws Exception {

        String sql = """
                UPDATE PAYMENT_METHOD
                SET PAYMENT_DEFAULT = 'N'
                WHERE MEMBER_NO = ?
                  AND PAYMENT_TYPE = 'BANK_TRANSFER'
                  AND PAYMENT_DEFAULT = 'Y'
                """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberNo);

            return pstmt.executeUpdate();
        }
    }


    /*
     * 계좌 등록
     */
    public int insertBankAccount(
    		Connection conn,
    		PaymentMethodDTO dto)
    		throws Exception {

    	String sql = """
    			INSERT INTO PAYMENT_METHOD (
    				PAYMENT_METHOD_NO,
    				MEMBER_NO,
    				PAYMENT_TYPE,
    				BANK_CODE,
    				ACCOUNT_LAST4,
    				ACCOUNT_HOLDER,
    				PAYMENT_DEFAULT,
    				CREATED_AT
    			)
    			VALUES (
    				SEQ_PAYMENT_METHOD_NO.NEXTVAL,
    				?,
    				'BANK_TRANSFER',
    				?,
    				?,
    				?,
    				?,
    				SYSDATE
    			)
    			""";

    	try (PreparedStatement pstmt =
    			 conn.prepareStatement(sql)) {

    		pstmt.setInt(
    				1,
    				dto.getMemberNo()
    		);

    		pstmt.setString(
    				2,
    				dto.getBankCode()
    		);

    		pstmt.setString(
    				3,
    				dto.getAccountLast4()
    		);

    		pstmt.setString(
    				4,
    				dto.getAccountHolder()
    		);

    		pstmt.setString(
    				5,
    				dto.isPaymentDefault()
    					? "Y"
    					: "N"
    		);

    		return pstmt.executeUpdate();
    	}
    }


    /*
     * 주문할 때 해당 회원의 결제수단인지 검사
     */
    public boolean existsPaymentMethod(
            Connection conn,
            int paymentMethodNo,
            int memberNo)
            throws Exception {

        String sql = """
                SELECT 1
                FROM PAYMENT_METHOD
                WHERE PAYMENT_METHOD_NO = ?
                  AND MEMBER_NO = ?
                  AND PAYMENT_TYPE = 'BANK_TRANSFER'
                """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setInt(1, paymentMethodNo);
            pstmt.setInt(2, memberNo);

            try (ResultSet rs =
                     pstmt.executeQuery()) {

                return rs.next();
            }
        }
    }
    
    public int insertCard(
    		Connection conn,
    		PaymentMethodDTO dto)
    		throws Exception {

    	String sql = """
    			INSERT INTO PAYMENT_METHOD (
    				PAYMENT_METHOD_NO,
    				MEMBER_NO,
    				PAYMENT_TYPE,
    				CARD_COMPANY,
    				CARD_LAST4,
    				PAYMENT_DEFAULT,
    				CREATED_AT
    			)
    			VALUES (
    				SEQ_PAYMENT_METHOD_NO.NEXTVAL,
    				?,
    				'CARD',
    				?,
    				?,
    				?,
    				SYSDATE
    			)
    			""";

    	try (PreparedStatement pstmt =
    			 conn.prepareStatement(sql)) {

    		pstmt.setInt(
    				1,
    				dto.getMemberNo()
    		);

    		pstmt.setString(
    				2,
    				dto.getCardCompany()
    		);

    		pstmt.setString(
    				3,
    				dto.getCardLast4()
    		);

    		pstmt.setString(
    				4,
    				dto.isPaymentDefault()
    					? "Y"
    					: "N"
    		);

    		return pstmt.executeUpdate();
    	}
    }
    
    public int clearDefault(
    		Connection conn,
    		int memberNo,
    		String paymentType)
    		throws Exception {

    	String sql = """
    			UPDATE PAYMENT_METHOD
    			SET PAYMENT_DEFAULT = 'N'
    			WHERE MEMBER_NO = ?
    			  AND PAYMENT_TYPE = ?
    			  AND PAYMENT_DEFAULT = 'Y'
    			""";

    	try (PreparedStatement pstmt =
    			 conn.prepareStatement(sql)) {

    		pstmt.setInt(1, memberNo);
    		pstmt.setString(2, paymentType);

    		return pstmt.executeUpdate();
    	}
    }
    
    public List<PaymentMethodDTO> getCards(int memberNo) {

    	List<PaymentMethodDTO> list = new ArrayList<>();

    	String sql = """
    			SELECT
    				PAYMENT_METHOD_NO,
    				MEMBER_NO,
    				PAYMENT_TYPE,
    				CARD_COMPANY,
    				CARD_LAST4,
    				PAYMENT_DEFAULT
    			FROM PAYMENT_METHOD
    			WHERE MEMBER_NO = ?
    			  AND PAYMENT_TYPE = 'CARD'
    			ORDER BY
    				CASE
    					WHEN PAYMENT_DEFAULT = 'Y' THEN 0
    					ELSE 1
    				END,
    				CREATED_AT DESC
    			""";

    	try (
    		Connection conn = ConnectionProvider.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)
    	) {

    		pstmt.setInt(1, memberNo);

    		try (ResultSet rs = pstmt.executeQuery()) {

    			while (rs.next()) {

    				PaymentMethodDTO dto =
    						new PaymentMethodDTO();

    				dto.setPaymentMethodNo(
    						rs.getInt("PAYMENT_METHOD_NO")
    				);

    				dto.setMemberNo(
    						rs.getInt("MEMBER_NO")
    				);

    				dto.setPaymentType(
    						rs.getString("PAYMENT_TYPE")
    				);

    				dto.setCardCompany(
    						rs.getString("CARD_COMPANY")
    				);

    				dto.setCardLast4(
    						rs.getString("CARD_LAST4")
    				);

    				dto.setPaymentDefault(
    						"Y".equals(
    								rs.getString("PAYMENT_DEFAULT")
    						)
    				);

    				list.add(dto);
    			}
    		}

    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return list;
    }
    
}