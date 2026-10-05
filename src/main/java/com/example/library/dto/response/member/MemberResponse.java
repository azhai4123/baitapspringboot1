package com.example.library.dto.response.member;

import java.time.LocalDateTime;

public record MemberResponse(Long id, String fullName, String email, String phone,
                            String status, LocalDateTime createdAt) {
}
