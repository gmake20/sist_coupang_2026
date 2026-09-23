package org.doit.goodpang.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.domain.VendorDailySalesDTO;
import org.doit.goodpang.domain.VendorDailyTrafficDTO;
import org.doit.goodpang.domain.VendorDashboardStatDTO;
import org.doit.goodpang.domain.VendorOrderStatSummaryDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorDashboardMapper {
	public VendorDashboardStatDTO getTodayStat(@Param("sellerNo") int sellerNo,@Param("targetDate") Date targetDate);

	public List<VendorDailySalesDTO> getDailySalesStat(@Param("sellerNo") int sellerNo,@Param("targetDate")  Date targetDate);

	public List<VendorDailyTrafficDTO> getDailyTrafficStat(@Param("sellerNo") int sellerNo,@Param("targetDate")  Date targetDate);

	public List<VendorDailySalesDTO> getWeeklySalesStat(@Param("sellerNo") int sellerNo,@Param("targetDate")  Date targetDate);

	public List<VendorDailySalesDTO> getMonthlySalesStat(@Param("sellerNo") int sellerNo,@Param("targetDate")  Date targetDate);

	public VendorOrderStatSummaryDTO countOrderStats(@Param("sellerNo") int sellerNo);

	public List<NoticeDTO> findRecentNotices(@Param("limit") int limit);

}
