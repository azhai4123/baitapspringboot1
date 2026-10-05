package com.example.library.service.member;

import com.example.library.dto.request.member.MemberCreateRequest;
import com.example.library.dto.request.member.MemberUpdateRequest;
import com.example.library.dto.response.member.MemberResponse;
import com.example.library.entity.Member;
import com.example.library.exception.MemberAlreadyExistsException;
import com.example.library.exception.MemberNotFoundException;
import com.example.library.mapper.member.MemberMapper;
import com.example.library.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class MemberService {
    private final MemberRepository repository;
    private final MemberMapper mapper;

    public MemberService(MemberRepository repository, MemberMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> search(String query, String status, Pageable pageable) {
        return repository.search(normalize(query), normalize(status), pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MemberResponse get(Long id) { return mapper.toResponse(find(id)); }

    @Transactional
    public MemberResponse create(MemberCreateRequest request) {
        ensureEmailAvailable(request.getEmail(), null);
        return mapper.toResponse(repository.save(new Member(request.getFullName().trim(),
                request.getEmail().trim().toLowerCase(Locale.ROOT), trimToNull(request.getPhone()))));
    }

    @Transactional
    public MemberResponse update(Long id, MemberUpdateRequest request) {
        Member member = find(id);
        ensureEmailAvailable(request.getEmail(), id);
        member.setFullName(request.getFullName().trim());
        member.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        member.setPhone(trimToNull(request.getPhone()));
        return mapper.toResponse(member);
    }

    @Transactional
    public MemberResponse setBlocked(Long id, boolean blocked) {
        Member member = find(id);
        member.setStatus(blocked ? Member.BLOCKED : Member.ACTIVE);
        return mapper.toResponse(member);
    }

    private Member find(Long id) {
        return repository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
    }

    private void ensureEmailAvailable(String email, Long id) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        boolean exists = id == null ? repository.existsByEmailIgnoreCase(normalized)
                : repository.existsByEmailIgnoreCaseAndIdNot(normalized, id);
        if (exists) throw new MemberAlreadyExistsException(normalized);
    }

    private static String normalize(String value) { return value == null ? "" : value.trim(); }
    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
