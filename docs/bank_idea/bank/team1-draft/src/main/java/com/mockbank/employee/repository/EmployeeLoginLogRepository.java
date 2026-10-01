package com.mockbank.employee.repository;

import com.mockbank.employee.domain.EmployeeLoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeLoginLogRepository extends JpaRepository<EmployeeLoginLog, Long> {

    /** 직원별 로그인 이력 (IX_ELL_EMPLOYEE) */
    Page<EmployeeLoginLog> findByEmployee_IdOrderByLoginAtDesc(Long employeeId, Pageable pageable);

    /** 관리자 활동 로그 화면 - 전체 직원 로그인 이력 (IX_ELL_LOGIN_AT) */
    Page<EmployeeLoginLog> findAllByOrderByLoginAtDesc(Pageable pageable);
}
