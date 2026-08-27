package com.goodpang.dto;

import java.util.Date;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    
	private int reviewNo;
    private int productRating;
    private Integer serviceRating;
    private String reviewContent;
    private String reviewSummary;
    private Date reviewDate;
    private int memberNo;
    private int orderDetailNo;
    
    private String ratingStars;
    private String maskedName;
    private String optionText;
    
    private int productNo;
    private String productName;
}