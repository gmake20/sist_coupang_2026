package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.ReviewDTO;
import com.goodpang.util.ConnectionProvider;

public class ReviewDAO {

    public List<ReviewDTO> selectReviewsByProductNo(int productNo) {

        // 2026-08-27: 리뷰 카드에 판매자 상호명·상품명도 나와야 해서 PRODUCT·SELLER 조인 추가.
        // (지금은 상품 상세페이지라 값이 페이지 전체에서 다 같지만, 리뷰 카드 자체에도 있어야 한다는
        //  요구라 조인해서 내려줌 — PRODUCT/SELLER 는 od.PRODUCT_NO 기준으로 항상 있는 값이라 INNER JOIN)
        String sql = """
            SELECT r.REVIEW_NO, r.PRODUCT_RATING, r.REVIEW_CONTENT, r.REVIEW_DATE, r.REVIEW_SUMMARY,
                   m.MEMBER_NAME,
                   p.PRODUCT_NAME,
                   s.STORE_NAME,
                   po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE
            FROM REVIEW r
            JOIN ORDER_DETAIL od ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
            JOIN MEMBER m ON r.MEMBER_NO = m.MEMBER_NO
            JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO
            JOIN SELLER s ON p.SELLER_NO = s.SELLER_NO
            LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID
            WHERE od.PRODUCT_NO = ?
            ORDER BY r.REVIEW_DATE DESC
            """;

        List<ReviewDTO> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setReviewNo(rs.getInt("REVIEW_NO"));

                    /* 2026-08-27: 별점을 "★★★★☆" 문자열로 미리 만들어두던 것 → 화면이
                       스프라이트 이미지(width%)로 바뀌면서 필요없어짐. rating 정수만 내려주면
                       product.jsp 에서 ${r.rating * 20}% 로 그때그때 계산해서 씀 */
                    dto.setRating(rs.getInt("PRODUCT_RATING"));

                    dto.setReviewContent(rs.getString("REVIEW_CONTENT"));
                    dto.setReviewContent(rs.getString("REVIEW_SUMMARY"));
                    dto.setReviewDate(sdf.format(rs.getDate("REVIEW_DATE")));
                    dto.setMaskedName(maskName(rs.getString("MEMBER_NAME")));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setStoreName(rs.getString("STORE_NAME"));

                    String o1v = rs.getString("OPTION1_VALUE");
                    String o2v = rs.getString("OPTION2_VALUE");
                    StringBuilder option = new StringBuilder();
                    if (o1v != null) option.append(rs.getString("OPTION1_TYPE")).append(" ").append(o1v);
                    if (o2v != null) {
                        if (option.length() > 0) option.append(" / ");
                        option.append(rs.getString("OPTION2_TYPE")).append(" ").append(o2v);
                    }
                    dto.setOptionText(option.toString());

                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // "홍길동" → "홍*동", "이연" → "이*"
    private String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) sb.append("*");
        sb.append(name.charAt(name.length() - 1));
        return sb.toString();
    }
}