package org.doit.goodpang.domain.security;

import java.util.Collection;
import java.util.Collections;

import org.doit.goodpang.domain.MemberVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CustomUser extends User {

    private static final long serialVersionUID = 1L;

    private MemberVO member;

    public CustomUser(MemberVO member) {

        super(
            member.getMemberId(),
            member.getMemberPw(),
            member.getStatus() == 1,
            true,
            true,
            true,
            Collections.singletonList(
                new SimpleGrantedAuthority(member.getRank())
            )
        );

        this.member = member;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }
}