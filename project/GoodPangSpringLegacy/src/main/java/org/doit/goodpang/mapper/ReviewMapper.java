package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.ReviewDTO;
import org.doit.goodpang.domain.ReviewDTO2;
import org.doit.goodpang.domain.ReviewItemDTO;
import org.doit.goodpang.domain.ReviewRatingSummaryDTO;

public interface ReviewMapper {

    int countReviewsByMemberNo(
            @Param("memberNo") Long memberNo);

    int countAvailableReviewsByMemberNo(
            @Param("memberNo") Long memberNo);

    List<ReviewDTO2> selectReviewsByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    List<ReviewItemDTO> selectAvailableReviewsByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
    
    ReviewItemDTO getReviewItem(
            @Param("orderDetailNo") Integer orderDetailNo,
            @Param("productNo") Integer productNo,
            @Param("memberNo") Long memberNo);

    int existsByOrderDetailNo(
            @Param("orderDetailNo") Integer orderDetailNo);

    int insertReview(ReviewDTO dto);

    int insertReviewImage(
            @Param("reviewNo") int reviewNo,
            @Param("imageUrl") String imageUrl,
            @Param("imageOrder") int imageOrder);
    
    int deleteReviewImages(
            @Param("reviewNo") Integer reviewNo,
            @Param("memberNo") Long memberNo);

    int deleteReview(
            @Param("reviewNo") Integer reviewNo,
            @Param("memberNo") Long memberNo);

    int deleteReviewImagesByAdmin(
            @Param("reviewNo") Integer reviewNo);

    int deleteReviewByAdmin(
            @Param("reviewNo") Integer reviewNo);

    ReviewDTO2 selectReviewByNo(
            @Param("reviewNo") Integer reviewNo,
            @Param("memberNo") Long memberNo);

    int updateReview(
            @Param("review") ReviewDTO review,
            @Param("memberNo") Long memberNo);
    
    List<ReviewDTO2> selectReviewsByProductNo(
            @Param("productNo")
            Integer productNo);

    ReviewRatingSummaryDTO getRatingSummary(
            @Param("productNo")
            Integer productNo);
}