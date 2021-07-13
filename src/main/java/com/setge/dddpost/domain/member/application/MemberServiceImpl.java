package com.setge.dddpost.domain.member.application;

import com.setge.dddpost.domain.member.application.MemberDto.CheckEmail;
import com.setge.dddpost.domain.member.application.MemberDto.CheckNickname;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.member.domain.MemberDomainService;
import com.setge.dddpost.domain.member.domain.MemberValidator;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MemberValidator validator;
  private final MemberDomainService domainService;


  @Transactional
  @Override
  public Response joinMember(Join dto) {
    Member member = dto.toEntity();
    member.create(validator);
    return getMember(memberRepository.save(member).getId());
  }

  @Override
  public void checkEmail(CheckEmail dto) {
    domainService.emailCheck(dto.getEmail());
  }

  @Override
  public void checkNickname(CheckNickname dto) {
    domainService.nicknameCheck(dto.getNickname());
  }

  @Override
  public Response getMember(final Long id) {
    return toResponse(domainService.getMember(id));
  }

  @Transactional
  @Override
  public Response updateMember(final Long id, Update dto) {
    Member existingMember = domainService.getMember(id);
    domainService.nicknameCheck(dto.getNickname());
    dto.update(existingMember);
    return getMember(id);
  }

  @Transactional
  @Override
  public void leaveMember(final Long id) {
    Member member = domainService.getMember(id);
    member.leaveMember();
  }

  @Override
  public Page<Response> findAllMembers(Pageable pageable) {
    Page<MemberSearchDto> members = domainService.getMembers(pageable);
    return new PageImpl<>(toResponses(members), pageable, members.getTotalElements());
  }

  @Override
  public Page<Response> retrieveMember(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<MemberSearchDto> search = domainService.search(searchCondition, pageable);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  private Response toResponse(Member member) {
    return new Response(member);
  }

  private List<Response> toResponses(Page<MemberSearchDto> members) {
    return members.stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

}
