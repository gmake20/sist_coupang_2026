package com.mockbank.employee.domain;

import com.mockbank.branch.domain.Branch;
import com.mockbank.branch.domain.BranchType;
import com.mockbank.common.domain.LoginLock;
import com.mockbank.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.YesNoConverter;

/**
 * 은행 직원. 고객(Member)과 테이블·로그인을 분리한다 (/admin/login, 사번으로 로그인).
 * 직원 계정 생성·권한 변경은 SYSTEM_ADMIN 만 한다 (Service 의 @PreAuthorize 로 검사).
 */
@Getter
@Entity
@Table(name = "EMPLOYEE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EMPLOYEE_GEN")
    @SequenceGenerator(name = "SEQ_EMPLOYEE_GEN", sequenceName = "SEQ_EMPLOYEE", allocationSize = 1)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    /** 사번 - 직원 로그인 아이디 */
    @Column(name = "EMPLOYEE_NO", nullable = false, unique = true, length = 10, updatable = false)
    private String employeeNo;

    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @Column(name = "NAME", nullable = false, length = 30)
    private String name;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PHONE", length = 20)
    private String phone;

    /** 소속 지점 (BRANCH_TYPE = BRANCH 만, 본점 포함) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BRANCH_ID", nullable = false)
    private Branch branch;

    /** 직급 - 표시용, 권한과 무관 */
    @Column(name = "POSITION", nullable = false, length = 20)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private EmployeeRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private EmployeeStatus status;

    /** 임시 비밀번호 상태 - 첫 로그인 시 비밀번호 변경 화면으로 보낸다 */
    @Convert(converter = YesNoConverter.class)
    @Column(name = "TEMP_PW_YN", nullable = false, length = 1)
    private Boolean passwordChangeRequired;

    @Embedded
    private LoginLock loginLock;

    @Column(name = "HIRED_AT", nullable = false)
    private LocalDate hiredAt;

    @Column(name = "RETIRED_AT")
    private LocalDate retiredAt;

    /** 직원 계정 생성 (SYSTEM_ADMIN). 임시 비밀번호로 만들고 첫 로그인 시 변경하게 한다. */
    public static Employee create(String employeeNo, String encodedTempPassword, String name, String email,
                                  String phone, Branch branch, String position, EmployeeRole role,
                                  LocalDate hiredAt) {
        Employee employee = new Employee();
        employee.employeeNo = employeeNo;
        employee.password = encodedTempPassword;
        employee.name = name;
        employee.email = email;
        employee.phone = phone;
        employee.assignBranch(branch);
        employee.position = position;
        employee.role = role;
        employee.status = EmployeeStatus.ACTIVE;
        employee.passwordChangeRequired = true;
        employee.loginLock = LoginLock.unlocked();
        employee.hiredAt = hiredAt;
        return employee;
    }

    // ===== 상태/권한 확인 =====

    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }

    /** 재직 중이고 required 권한 업무를 할 수 있는지 (MANAGER 는 STAFF 업무 포함) */
    public boolean hasAuthority(EmployeeRole required) {
        return isActive() && role.includes(required);
    }

    public boolean isLoginLocked() {
        return loginLock.isLocked();
    }

    // ===== 로그인 / 비밀번호 =====

    public void recordLoginFailure() {
        loginLock.recordFailure();
    }

    public void recordLoginSuccess() {
        loginLock.recordSuccess();
    }

    /** 본인이 비밀번호 변경 → 임시 비밀번호 상태 해제 */
    public void changePassword(String encodedPassword) {
        requireNotRetired();
        this.password = encodedPassword;
        this.passwordChangeRequired = false;
    }

    /** SYSTEM_ADMIN 이 비밀번호 초기화 → 임시 비밀번호 + 로그인 잠금 해제 */
    public void resetPassword(String encodedTempPassword) {
        requireNotRetired();
        this.password = encodedTempPassword;
        this.passwordChangeRequired = true;
        this.loginLock.unlock();
    }

    // ===== 인사 처리 (SYSTEM_ADMIN) =====

    public void changeRole(EmployeeRole newRole) {
        requireNotRetired();
        this.role = newRole;
    }

    public void transfer(Branch newBranch) {
        requireNotRetired();
        assignBranch(newBranch);
    }

    public void takeLeave() {
        if (status != EmployeeStatus.ACTIVE) {
            throw new IllegalStateException("재직 중인 직원만 휴직 처리할 수 있습니다.");
        }
        status = EmployeeStatus.ON_LEAVE;
    }

    public void returnFromLeave() {
        if (status != EmployeeStatus.ON_LEAVE) {
            throw new IllegalStateException("휴직 중인 직원만 복직 처리할 수 있습니다.");
        }
        status = EmployeeStatus.ACTIVE;
    }

    /** 퇴사. 심사 이력이 이 직원을 참조하므로 행은 지우지 않는다. */
    public void retire(LocalDate retiredAt) {
        requireNotRetired();
        if (retiredAt.isBefore(hiredAt)) {
            throw new IllegalArgumentException("퇴사일은 입사일보다 빠를 수 없습니다.");
        }
        this.status = EmployeeStatus.RETIRED;
        this.retiredAt = retiredAt;
    }

    private void assignBranch(Branch branch) {
        if (branch == null || branch.getBranchType() != BranchType.BRANCH) {
            throw new IllegalArgumentException("직원의 소속은 지점(BRANCH)이어야 합니다.");
        }
        this.branch = branch;
    }

    private void requireNotRetired() {
        if (status == EmployeeStatus.RETIRED) {
            throw new IllegalStateException("퇴사한 직원입니다.");
        }
    }
}
