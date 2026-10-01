package com.mockbank.branch.domain;

import com.mockbank.common.entity.BaseTimeEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.YesNoConverter;

/**
 * 지점/ATM. 고객의 지점/ATM 찾기 화면과 직원의 소속 지점(EMPLOYEE.BRANCH_ID)에 쓰인다.
 * 이용 가능 업무는 BRANCH_SERVICE 테이블에 코드로 저장한다.
 */
@Getter
@Entity
@Table(name = "BRANCH")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Branch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BRANCH_GEN")
    @SequenceGenerator(name = "SEQ_BRANCH_GEN", sequenceName = "SEQ_BRANCH", allocationSize = 1)
    @Column(name = "BRANCH_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "BRANCH_TYPE", nullable = false, length = 10, updatable = false)
    private BranchType branchType;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "ADDRESS", nullable = false, length = 200)
    private String address;

    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "BUSINESS_HOURS", length = 50)
    private String businessHours;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "OPEN_24H_YN", nullable = false, length = 1)
    private Boolean open24h;

    /** 이용 가능 업무 (BRANCH_SERVICE 테이블). 수정은 update() 로만 한다. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "BRANCH_SERVICE", joinColumns = @JoinColumn(name = "BRANCH_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "SERVICE_CODE", nullable = false, length = 30)
    private Set<BranchServiceType> services = new HashSet<>();

    @Convert(converter = YesNoConverter.class)
    @Column(name = "USE_YN", nullable = false, length = 1)
    private Boolean inUse;

    public static Branch create(BranchType branchType, String name, String address,
                                BigDecimal latitude, BigDecimal longitude, String phone,
                                String businessHours, boolean open24h, Set<BranchServiceType> services) {
        Branch branch = new Branch();
        branch.branchType = branchType;
        branch.inUse = true;
        branch.update(name, address, latitude, longitude, phone, businessHours, open24h, services);
        return branch;
    }

    public void update(String name, String address, BigDecimal latitude, BigDecimal longitude,
                       String phone, String businessHours, boolean open24h, Set<BranchServiceType> services) {
        validateServices(services);
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.businessHours = businessHours;
        this.open24h = open24h;
        // 컬렉션 객체는 유지하고 내용만 교체해야 Hibernate 가 변경분만 반영한다
        this.services.clear();
        this.services.addAll(services);
    }

    /** 화면 표시 순서(enum 선언 순서)로 정렬된 읽기 전용 목록 */
    public Set<BranchServiceType> getServices() {
        if (services.isEmpty()) {
            return Collections.unmodifiableSet(EnumSet.noneOf(BranchServiceType.class));
        }
        return Collections.unmodifiableSet(EnumSet.copyOf(services));
    }

    public boolean provides(BranchServiceType service) {
        return services.contains(service);
    }

    /** 삭제 대신 미사용 처리. 소속 직원이 있는지는 Service 에서 먼저 검사한다. */
    public void disable() {
        this.inUse = false;
    }

    private void validateServices(Set<BranchServiceType> services) {
        if (services == null || services.isEmpty()) {
            throw new IllegalArgumentException("이용 가능 업무를 1개 이상 선택해야 합니다.");
        }
        for (BranchServiceType service : services) {
            if (!service.isAvailableAt(branchType)) {
                throw new IllegalArgumentException(service.getLabel() + "은(는) 지점 창구에서만 가능한 업무입니다.");
            }
        }
    }
}
