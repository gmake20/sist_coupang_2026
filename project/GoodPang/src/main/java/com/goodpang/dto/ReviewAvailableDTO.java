package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewAvailableDTO {

	private int orderDetailNo;
    private int productNo;
    private String productName;
    private boolean reviewWritten;

    public boolean isReviewWritten() {
        return reviewWritten;
    }

    public void setReviewWritten(boolean reviewWritten) {
        this.reviewWritten = reviewWritten;
    }
}
