package org.doit.goodpang.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.doit.goodpang.domain.ReviewDTO;
import org.doit.goodpang.domain.ReviewDTO2;
import org.doit.goodpang.domain.ReviewItemDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.ReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
@Log4j
public class ReviewController {

    private static final int PAGE_SIZE = 5;
    private static final int PAGE_BLOCK = 5;

    private final ReviewService reviewService;
    
    @Value("${upload.base-dir}")
    private String uploadBaseDir;

    @GetMapping("/write")
    public String write(
            @RequestParam("orderDetailNo") Integer orderDetailNo,
            @RequestParam("productNo") Integer productNo,
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {
            return "redirect:/login";
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        ReviewItemDTO reviewItem =
                reviewService.getReviewItem(
                        orderDetailNo,
                        productNo,
                        memberNo
                );

        if (reviewItem == null) {
            throw new IllegalArgumentException(
                    "리뷰 작성 대상 상품을 찾을 수 없습니다."
            );
        }

        int reviewCount =
                reviewService.countReviewsByMemberNo(
                        memberNo
                );

        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("reviewItem", reviewItem);

        return "review.write";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("orderDetailNo") Integer orderDetailNo,
            @RequestParam("productRating") Integer productRating,
            @RequestParam(value = "serviceRating", required = false)
            Integer serviceRating,
            @RequestParam("reviewContent") String reviewContent,
            @RequestParam(value = "reviewSummary", required = false)
            String reviewSummary,
            @RequestParam(value = "reviewImages", required = false)
            List<MultipartFile> reviewImages,
            Authentication authentication,
            RedirectAttributes rttr)
            throws IOException {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {
            return "redirect:/login";
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        if (orderDetailNo == null
                || productRating == null
                || reviewContent == null
                || reviewContent.isBlank()) {
            throw new IllegalArgumentException(
                    "필수 리뷰 정보가 없습니다."
            );
        }

        if (productRating < 1 || productRating > 5) {
            throw new IllegalArgumentException(
                    "상품 별점이 올바르지 않습니다."
            );
        }

        if (serviceRating != null
                && (serviceRating < 1 || serviceRating > 2)) {
            throw new IllegalArgumentException(
                    "서비스 만족도가 올바르지 않습니다."
            );
        }

        if (reviewService.existsByOrderDetailNo(orderDetailNo)) {
            throw new IllegalStateException(
                    "이미 작성한 리뷰입니다."
            );
        }

        ReviewDTO2 dto = new ReviewDTO2();

        dto.setOrderDetailNo(orderDetailNo);
        dto.setMemberNo(memberNo.intValue());
        dto.setProductRating(productRating);
        dto.setServiceRating(serviceRating);
        dto.setReviewContent(reviewContent.trim());

        if (reviewSummary != null
                && !reviewSummary.isBlank()) {
            dto.setReviewSummary(reviewSummary.trim());
        }

        int reviewNo =
                reviewService.insertReview(dto);

        List<String> imageUrls =
                saveUploadedFiles(
                        reviewImages,
                        reviewNo
                );

        reviewService.insertReviewImages(
                reviewNo,
                imageUrls
        );

        rttr.addFlashAttribute(
                "message",
                "리뷰가 등록되었습니다."
        );

        return "redirect:/review/list?tab=written";
    }

    private List<String> saveUploadedFiles(
            List<MultipartFile> reviewImages,
            int reviewNo)
            throws IOException {

        List<String> imageUrls =
                new ArrayList<>();

        if (reviewImages == null
                || reviewImages.isEmpty()) {
            return imageUrls;
        }

        int count = 0;

        for (MultipartFile file : reviewImages) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            if (count >= 10) {
                throw new IllegalArgumentException(
                        "사진은 최대 10장까지 첨부할 수 있습니다."
                );
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "이미지 한 장의 최대 크기는 10MB입니다."
                );
            }

            String contentType =
                    file.getContentType();

            if (contentType == null
                    || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException(
                        "이미지 파일만 업로드할 수 있습니다."
                );
            }

            String originalName =
                    file.getOriginalFilename();

            if (originalName == null
                    || originalName.isBlank()) {
                continue;
            }

            originalName =
                    new File(originalName)
                        .getName();

            String ext = "";

            int dotIndex =
                    originalName.lastIndexOf('.');

            if (dotIndex >= 0) {
                ext =
                    originalName
                        .substring(dotIndex)
                        .toLowerCase();
            }

            if (!ext.equals(".jpg")
                    && !ext.equals(".jpeg")
                    && !ext.equals(".png")
                    && !ext.equals(".gif")
                    && !ext.equals(".webp")) {

                throw new IllegalArgumentException(
                        "jpg, jpeg, png, gif, webp 이미지만 업로드할 수 있습니다."
                );
            }

            String savedName =
                    UUID.randomUUID() + ext;

            File uploadDir =
                    new File(
                            uploadBaseDir,
                            "review"
                            + File.separator
                            + reviewNo
                    );

            if (!uploadDir.exists()
                    && !uploadDir.mkdirs()) {

                throw new IOException(
                        "리뷰 이미지 저장 폴더를 생성할 수 없습니다."
                );
            }

            File savedFile =
                    new File(
                            uploadDir,
                            savedName
                    );

            file.transferTo(savedFile);

            imageUrls.add(
                    "/upload/review/"
                    + reviewNo
                    + "/"
                    + savedName
            );

            count++;
        }

        return imageUrls;
    }

    @GetMapping("/list")
    public String reviewList(
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "page", required = false) String pageParam,
            Authentication authentication,
            Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {
            return "redirect:/login";
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        if (!"available".equals(tab)
                && !"written".equals(tab)) {
            tab = "written";
        }

        int page = 1;

        try {
            if (pageParam != null && !pageParam.isBlank()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        if (page < 1) {
            page = 1;
        }

        int availableCount =
                reviewService.countAvailableReviewsByMemberNo(memberNo);

        int writtenCount =
                reviewService.countReviewsByMemberNo(memberNo);

        int totalCount =
                "available".equals(tab)
                        ? availableCount
                        : writtenCount;

        int totalPages =
                (int) Math.ceil(
                        (double) totalCount / PAGE_SIZE
                );

        if (totalPages < 1) {
            totalPages = 1;
        }

        if (page > totalPages) {
            page = totalPages;
        }

        int offset =
                (page - 1) * PAGE_SIZE;

        if ("available".equals(tab)) {

            List<ReviewItemDTO> availableReviewList =
                    reviewService
                        .selectAvailableReviewsByMemberNo(
                                memberNo,
                                offset,
                                PAGE_SIZE
                        );

            model.addAttribute(
                    "availableReviewList",
                    availableReviewList
            );

        } else {

            List<ReviewDTO2> writtenReviewList =
                    reviewService
                        .selectReviewsByMemberNo(
                                memberNo,
                                offset,
                                PAGE_SIZE
                        );

            model.addAttribute(
                    "writtenReviewList",
                    writtenReviewList
            );
        }

        int startPage =
                ((page - 1) / PAGE_BLOCK)
                        * PAGE_BLOCK + 1;

        int endPage =
                Math.min(
                        startPage + PAGE_BLOCK - 1,
                        totalPages
                );

        model.addAttribute("availableCount", availableCount);
        model.addAttribute("writtenCount", writtenCount);
        model.addAttribute("activeTab", tab);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        model.addAttribute("memberName", customUser.getMember().getMemberName() );

        return "review.list";
    }
    
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam("reviewNo") Integer reviewNo,
            Authentication authentication,
            HttpServletResponse response)
            throws IOException {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {
            return "redirect:/login";
        }

        if (reviewNo == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "리뷰 번호가 없습니다."
            );
            return null;
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        boolean isAdmin =
                authentication.getAuthorities()
                    .stream()
                    .anyMatch(auth ->
                            "ROLE_ADMIN".equals(auth.getAuthority()));

        int rowCount =
                reviewService.deleteReview(
                        reviewNo,
                        memberNo,
                        isAdmin
                );

        if (rowCount == 0) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "삭제할 리뷰가 없거나 삭제 권한이 없습니다."
            );
            return null;
        }

        return "redirect:/review/list?tab=written";
    }
    
    @GetMapping("/edit")
    public String edit(
            @RequestParam(value = "reviewNo", required = false) Integer reviewNo,
            Authentication authentication,
            HttpServletResponse response,
            Model model) throws IOException {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {
            return "redirect:/login";
        }

        if (reviewNo == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "리뷰 번호가 없습니다."
            );
            return null;
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        ReviewDTO2 review =
                reviewService.selectReviewByNo(
                        reviewNo,
                        memberNo
                );

        if (review == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "리뷰를 찾을 수 없거나 수정 권한이 없습니다."
            );
            return null;
        }

        model.addAttribute("review", review);

        return "review/review_edit";
    }

    @PostMapping("/edit")
    public String edit(
            @RequestParam(value = "reviewNo", required = false) Integer reviewNo,
            @RequestParam(value = "productRating", required = false) Integer productRating,
            @RequestParam(value = "serviceRating", required = false) Integer serviceRating,
            @RequestParam(value = "reviewContent", required = false) String reviewContent,
            @RequestParam(value = "reviewSummary", required = false) String reviewSummary,
            Authentication authentication,
            HttpServletResponse response) throws IOException {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {
            return "redirect:/login";
        }

        if (reviewNo == null
                || productRating == null
                || reviewContent == null
                || reviewContent.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "필수 리뷰 정보가 없습니다."
            );
            return null;
        }

        if (productRating < 1 || productRating > 5) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "상품 별점이 올바르지 않습니다."
            );
            return null;
        }

        if (serviceRating != null
                && serviceRating != 1
                && serviceRating != 2) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "서비스 평가가 올바르지 않습니다."
            );
            return null;
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        ReviewDTO2 dto = new ReviewDTO2();

        dto.setReviewNo(reviewNo);
        dto.setProductRating(productRating);
        dto.setServiceRating(serviceRating);
        dto.setReviewContent(reviewContent.trim());

        if (reviewSummary != null) {
            dto.setReviewSummary(reviewSummary.trim());
        }

        int rowCount =
                reviewService.updateReview(
                        dto,
                        memberNo
                );

        if (rowCount == 0) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "리뷰를 찾을 수 없거나 수정 권한이 없습니다."
            );
            return null;
        }

        return "redirect:/review/list?tab=written";
    }
}