package com.example.library.mapper.member;

import com.example.library.dto.response.member.MemberResponse;
import com.example.library.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public MemberResponse toResponse(Member member) {
        return new MemberResponse(member.getId(), member.getFullName(), member.getEmail(),
                member.getPhone(), member.getStatus(), member.getCreatedAt());
    }
}
