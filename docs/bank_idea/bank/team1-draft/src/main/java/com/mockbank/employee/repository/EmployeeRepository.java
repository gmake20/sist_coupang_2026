package com.mockbank.employee.repository;

import com.mockbank.employee.domain.Employee;
import com.mockbank.employee.domain.EmployeeRole;
import com.mockbank.employee.domain.EmployeeStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** 직원 로그인 (직원용 UserDetailsService) */
    Optional<Employee> findByEmployeeNo(String employeeNo);

    /** 직원 등록 시 중복 확인 */
    boolean existsByEmployeeNo(String employeeNo);

    boolean existsByEmail(String email);

    /** 직원 관리 목록 (소속 지점을 함께 조회) */
    @EntityGraph(attributePaths = "branch")
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "branch")
    Page<Employee> findByBranch_Id(Long branchId, Pageable pageable);

    /** 마지막 남은 재직 SYSTEM_ADMIN 의 권한 변경·퇴사를 막을 때 사용 */
    long countByRoleAndStatus(EmployeeRole role, EmployeeStatus status);
}
