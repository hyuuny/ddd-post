package com.setge.dddpost.domain.member.application;

import com.setge.dddpost.domain.member.application.MemberDto.CheckEmail;
import com.setge.dddpost.domain.member.application.MemberDto.CheckNickname;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.member.domain.MemberValidator;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.member.infrastructure.MemberQueryRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MemberValidator memberValidator;
  private final MemberQueryRepository memberQueryRepository;


  @Transactional
  @Override
  public Response joinMember(Join dto) {
    Member member = dto.toEntity();
    memberValidator.validate(member);
    Long memberId = memberRepository.save(member).getId();
    return getMember(memberId);
  }

  @Override
  public void checkEmail(CheckEmail dto) {
    emailDuplicateCheck(dto.getEmail());
  }

  private void emailDuplicateCheck(String email) {
    memberRepository.findByEmail(email).ifPresent(member -> {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.email.duplicated");
    });
  }

  @Override
  public void checkNickname(CheckNickname dto) {
    nicknameDuplicateCheck(dto.getNickname());
  }

  private void nicknameDuplicateCheck(String nickname) {
    memberRepository.findByNickname(nickname).ifPresent(member -> {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.nickname.duplicated");
    });
  }

  @Override
  public Response getMember(final Long id) {
    Member member = findMemberById(id);
    return toResponse(member);
  }

  public Member findMemberById(Long id) {
    return memberRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.notFound", id));
  }

  private Response toResponse(Member member) {
    return new Response(member);
  }

  @Transactional
  @Override
  public Response updateMember(final Long id, Update dto) {
    Member existingMember = findMemberById(id);
    nicknameDuplicateCheck(dto.getNickname());
    dto.update(existingMember);
    return getMember(id);
  }

  @Transactional
  @Override
  public void leaveMember(final Long id) {
    Member member = findMemberById(id);
    member.leaveMember();
  }

  @Override
  public Page<Response> findAllMembers(Pageable pageable) {
    Page<MemberSearchDto> members = memberQueryRepository.findAllMembers(pageable);
    return new PageImpl<>(toResponses(members), pageable, members.getTotalElements());
  }

  @Override
  public Page<Response> retrieveMember(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<MemberSearchDto> search = memberQueryRepository.search(searchCondition, pageable);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  private List<Response> toResponses(Page<MemberSearchDto> members) {
    return members.stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

}
