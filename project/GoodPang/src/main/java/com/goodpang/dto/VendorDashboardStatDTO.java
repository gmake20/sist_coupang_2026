package com.goodpang.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 판매자센터 대시보드(vendor-dashboard.jsp) 상단 KPI 카드 중
 * "오늘 주문수" / "오늘 매출" 집계 결과. 어제 값과 비교해 증감률을 계산한다.
 * 주문취소(ORDER_STATUS='주문취소') 건은 집계에서 제외한다.
 */
@Getter
@Setter
public class VendorDashboardStatDTO {

    private int todayOrderCount;
    private long todaySales;

    private int yesterdayOrderCount;
    private long yesterdaySales;

    public int getOrderCountChangePercent() {
        return changePercent(todayOrderCount, yesterdayOrderCount);
    }

    public int getSalesChangePercent() {
        return changePercent(todaySales, yesterdaySales);
    }

    public boolean isOrderCountUp() {
        return todayOrderCount >= yesterdayOrderCount;
    }

    public boolean isSalesUp() {
        return todaySales >= yesterdaySales;
    }

    // 어제 값이 0이면 %가 정의되지 않으므로, 오늘도 0이면 0%, 오늘이 있으면 100%로 취급
    private int changePercent(long today, long yesterday) {

        if (yesterday == 0) {
            return (today == 0) ? 0 : 100;
        }

        return (int) Math.round(Math.abs(today - yesterday) * 100.0 / yesterday);
    }
}
