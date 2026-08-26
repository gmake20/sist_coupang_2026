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

        String sql = """
            SELECT r.REVIEW_NO, r.RATING, r.REVIEW_CONTENT, r.REVIEW_DATE,
                   m.MEMBER_NAME,
                   po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE
            FROM REVIEW r
            JOIN ORDER_DETAIL od ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
            JOIN MEMBER m ON r.MEMBER_NO = m.MEMBER_NO
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

                    int rating = rs.getInt("RATING");
                    dto.setRating(rating);
                    StringBuilder stars = new StringBuilder();
                    for (int i = 0; i < rating; i++) stars.append("★");
                    for (int i = rating; i < 5; i++) stars.append("☆");
                    dto.setRatingStars(stars.toString());

                    dto.setReviewContent(rs.getString("REVIEW_CONTENT"));
                    dto.setReviewDate(sdf.format(rs.getDate("REVIEW_DATE")));
                    dto.setMaskedName(maskName(rs.getString("MEMBER_NAME")));

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