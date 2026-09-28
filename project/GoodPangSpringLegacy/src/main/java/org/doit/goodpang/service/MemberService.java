package org.doit.goodpang.service;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.mapper.MemberMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    public MemberVO getMember(Long memberNo) {

        return memberMapper.getMember(memberNo);
    }
}