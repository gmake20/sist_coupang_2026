package com.mockbank.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 연속 실패 횟수와 잠금 상태. MEMBER, EMPLOYEE 가 같은 컬럼명으로 사용한다.
 * 5회 연속 실패하면 잠기고, 비밀번호 재설정으로 해제한다.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLock {

    public static final int MAX_FAIL_COUNT = 5;

    @Column(name = "LOGIN_FAIL_COUNT", nullable = false)
    private int failCount;

    @Column(name = "LOGIN_LOCKED_AT")
    private LocalDateTime lockedAt;

    public static LoginLock unlocked() {
        return new LoginLock();
    }

    public boolean isLocked() {
        return lockedAt != null;
    }

    /** 실패 1회 기록. 5회째에 잠근다. */
    public void recordFailure() {
        if (isLocked()) {
            return;
        }
        failCount++;
        if (failCount >= MAX_FAIL_COUNT) {
            lockedAt = LocalDateTime.now();
        }
    }

    public void recordSuccess() {
        failCount = 0;
    }

    public void unlock() {
        failCount = 0;
        lockedAt = null;
    }
}
