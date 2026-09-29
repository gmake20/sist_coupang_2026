package org.doit.goodpang.service;

import java.util.List;

import org.doit.goodpang.domain.ReviewDTO;
import org.doit.goodpang.domain.ReviewDTO2;
import org.doit.goodpang.domain.ReviewItemDTO;
import org.doit.goodpang.domain.ReviewRatingSummaryDTO;
import org.doit.goodpang.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    
    @Transactional(readOnly = true)
    public ReviewItemDTO getReviewItem(
            Integer orderDetailNo,
            Integer productNo,
            Long memberNo) {

        return reviewMapper.getReviewItem(
                orderDetailNo,
                productNo,
                memberNo
        );
    }

    @Transactional(readOnly = true)
    public boolean existsByOrderDetailNo(
            Integer orderDetailNo) {

        return reviewMapper.existsByOrderDetailNo(
                orderDetailNo
        ) > 0;
    }

    @Transactional
    public int insertReview(
           ReviewDTO2 dto) {

    	reviewMapper.insertReview(dto);

        return dto.getReviewNo();
    }
   

    @Transactional
    public void insertReviewImages(
            int reviewNo,
            List<String> imageUrls) {

        if (imageUrls == null
                || imageUrls.isEmpty()) {
            return;
        }

        int imageOrder = 1;

        for (String imageUrl : imageUrls) {

            reviewMapper.insertReviewImage(
                    reviewNo,
                    imageUrl,
                    imageOrder++
            );
        }
    }

    @Transactional(readOnly = true)
    public int countAvailableReviewsByMemberNo(
            Long memberNo) {

        return reviewMapper
                .countAvailableReviewsByMemberNo(
                        memberNo
                );
    }

    @Transactional(readOnly = true)
    public int countReviewsByMemberNo(
            Long memberNo) {

        return reviewMapper
                .countReviewsByMemberNo(
                        memberNo
                );
    }

    @Transactional(readOnly = true)
    public List<ReviewItemDTO> selectAvailableReviewsByMemberNo(
            Long memberNo,
            int offset,
            int pageSize) {

        return reviewMapper
                .selectAvailableReviewsByMemberNo(
                        memberNo,
                        offset,
                        pageSize
                );
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO2> selectReviewsByMemberNo(
            Long memberNo,
            int offset,
            int pageSize) {

        return reviewMapper
                .selectReviewsByMemberNo(
                        memberNo,
                        offset,
                        pageSize
                );
    }
    
    @Transactional
    public int deleteReview(
            Integer reviewNo,
            Long memberNo,
            boolean isAdmin) {

        if (isAdmin) {
            reviewMapper.deleteReviewImagesByAdmin(reviewNo);
            return reviewMapper.deleteReviewByAdmin(reviewNo);
        }

        reviewMapper.deleteReviewImages(
                reviewNo,
                memberNo
        );

        return reviewMapper.deleteReview(
                reviewNo,
                memberNo
        );
    }
    
    @Transactional(readOnly = true)
    public ReviewDTO2 selectReviewByNo(
            Integer reviewNo,
            Long memberNo) {

        return reviewMapper.selectReviewByNo(
                reviewNo,
                memberNo
        );
    }

    @Transactional
    public int updateReview(
            ReviewDTO2 dto,
            Long memberNo) {

        return reviewMapper.updateReview(
                dto,
                memberNo
        );
    }
    
    @Transactional(readOnly = true)
    public List<ReviewDTO2> selectReviewsByProductNo(
            Integer productNo) {

        List<ReviewDTO2> reviews =
                reviewMapper.selectReviewsByProductNo(
                        productNo
                );

        for (ReviewDTO2 review : reviews) {

            int rating =
                    review.getProductRating();

            StringBuilder stars =
                    new StringBuilder();

            for (int i = 0; i < rating; i++) {
                stars.append("★");
            }

            for (int i = rating; i < 5; i++) {
                stars.append("☆");
            }

            review.setRatingStars(
                    stars.toString()
            );

            review.setMaskedName(
                    maskName(
                            review.getMaskedName()
                    )
            );
        }

        return reviews;
    }

    @Transactional(readOnly = true)
    public ReviewRatingSummaryDTO getRatingSummary(
            Integer productNo) {

        return reviewMapper.getRatingSummary(
                productNo
        );
    }

    private String maskName(String name) {

        if (name == null
                || name.length() <= 1) {
            return name;
        }

        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }

        StringBuilder sb =
                new StringBuilder();

        sb.append(name.charAt(0));

        for (int i = 1;
                i < name.length() - 1;
                i++) {

            sb.append("*");
        }

        sb.append(
                name.charAt(
                        name.length() - 1
                )
        );

        return sb.toString();
    }
    
    @Transactional(readOnly = true)
    public List<ReviewDTO2> getWrittenReviews(
            Long memberNo,
            int offset,
            int pageSize) {

        List<ReviewDTO2> reviews =
                reviewMapper.selectReviewsByMemberNo(
                        memberNo,
                        offset,
                        pageSize
                );

        for (ReviewDTO2 review : reviews) {

            List<String> imageUrls =
                    reviewMapper.selectReviewImageUrls(
                            review.getReviewNo()
                    );

            review.setImageUrls(imageUrls);
        }

        return reviews;
    }
    
    @Transactional(readOnly = true)
    public List<ReviewDTO2> getReviewsWithImagesByProductNo(
            int productNo) {

        List<ReviewDTO2> reviews =
                reviewMapper.selectReviewsByProductNo(
                        productNo
                );

        for (ReviewDTO2 review : reviews) {

            List<String> imageUrls =
                    reviewMapper.selectReviewImageUrls(
                            review.getReviewNo()
                    );

            review.setImageUrls(imageUrls);
        }

        return reviews;
    }
}