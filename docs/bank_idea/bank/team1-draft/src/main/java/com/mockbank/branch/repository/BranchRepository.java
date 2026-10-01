package com.mockbank.branch.repository;

import com.mockbank.branch.domain.Branch;
import com.mockbank.branch.domain.BranchServiceType;
import com.mockbank.branch.domain.BranchType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    /**
     * 지점/ATM 찾기. 각 조건이 null 이면 해당 조건을 생략한다.
     *  - type     : 지점/ATM 구분
     *  - keyword  : 지점명 또는 주소에 포함
     *  - service  : 해당 업무를 제공하는 곳만 (예: 입금 가능한 ATM)
     *  - open24h  : true 면 24시간 운영하는 곳만
     * 이용 가능 업무 목록을 함께 조회해 화면 표시 시 추가 쿼리(N+1)가 나가지 않게 한다.
     */
    @Query("""
            select distinct b from Branch b
            left join fetch b.services
            where b.inUse = true
              and (:type is null or b.branchType = :type)
              and (:keyword is null
                   or b.name like concat('%', :keyword, '%')
                   or b.address like concat('%', :keyword, '%'))
              and (:service is null or :service member of b.services)
              and (:open24h is null or b.open24h = :open24h)
            order by b.name
            """)
    List<Branch> search(@Param("type") BranchType type,
                        @Param("keyword") String keyword,
                        @Param("service") BranchServiceType service,
                        @Param("open24h") Boolean open24h);
}
