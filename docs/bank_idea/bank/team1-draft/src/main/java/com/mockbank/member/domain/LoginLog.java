package com.mockbank.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.YesNoConverter;

/**
 * 로그인 이력. 추가만 하고 수정/삭제하지 않는다.
 * Spring Security 의 AuthenticationSuccessHandler / AuthenticationFailureHandler 에서 저장한다.
 */
@Getter
@Entity
@Table(name = "LOGIN_LOG")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLog {

    private static final int USER_AGENT_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LOGIN_LOG_GEN")
    @SequenceGenerator(name = "SEQ_LOGIN_LOG_GEN", sequenceName = "SEQ_LOGIN_LOG", allocationSize = 1)
    @Column(name = "LOGIN_LOG_ID")
    private Long id;

    /** 존재하지 않는 아이디로 시도한 경우 null */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", updatable = false)
    private Member member;

    @Column(name = "LOGIN_ID_INPUT", nullable = false, length = 30, updatable = false)
    private String loginIdInput;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "SUCCESS_YN", nullable = false, length = 1, updatable = false)
    private Boolean success;

    @Enumerated(EnumType.STRING)
    @Column(name = "FAIL_REASON", length = 30, updatable = false)
    private LoginFailReason failReason;

    @Column(name = "IP_ADDRESS", nullable = false, length = 45, updatable = false)
    private String ipAddress;

    @Column(name = "USER_AGENT", length = USER_AGENT_MAX_LENGTH, updatable = false)
    private String userAgent;

    @Column(name = "LOGIN_AT", nullable = false, updatable = false)
    private LocalDateTime loginAt;

    public static LoginLog success(Member member, String ipAddress, String userAgent) {
        return create(member, member.getLoginId(), true, null, ipAddress, userAgent);
    }

    /** @param member 존재하지 않는 아이디면 null */
    public static LoginLog failure(Member member, String loginIdInput, LoginFailReason reason,
                                   String ipAddress, String userAgent) {
        if (reason == null) {
            throw new IllegalArgumentException("실패 사유가 필요합니다.");
        }
        return create(member, loginIdInput, false, reason, ipAddress, userAgent);
    }

    private static LoginLog create(Member member, String loginIdInput, boolean success, LoginFailReason reason,
                                   String ipAddress, String userAgent) {
        LoginLog log = new LoginLog();
        log.member = member;
        log.loginIdInput = truncate(loginIdInput, 30);
        log.success = success;
        log.failReason = reason;
        log.ipAddress = ipAddress;
        log.userAgent = truncate(userAgent, USER_AGENT_MAX_LENGTH);
        log.loginAt = LocalDateTime.now();
        return log;
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
