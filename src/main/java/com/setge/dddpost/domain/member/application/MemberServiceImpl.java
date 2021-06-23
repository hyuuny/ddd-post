package com.setge.dddpost.domain.member.application;

import com.setge.dddpost.domain.member.application.MemberDto.CheckEmail;
import com.setge.dddpost.domain.member.application.MemberDto.CheckNickname;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.member.domain.MemberMapper;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.member.infrastructure.MemberQueryRepository;
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
  private final MemberMapper memberMapper;
  private final MemberQueryRepository memberQueryRepository;


  @Transactional
  @Override
  public Response joinMember(Join dto) {
    memberMapper.checkEmailAndNicknameDuplicated(dto.getEmail(), dto.getNickname());
    Member member = dto.toEntity();
    Long memberId = memberRepository.save(member).getId();
    return getMember(memberId);
  }

  @Override
  public void checkEmail(CheckEmail dto) {
    memberMapper.checkEmailDuplicated(dto.getEmail());
  }

  @Override
  public void checkNickname(CheckNickname dto) {
    memberMapper.checkNicknameDuplicated(dto.getNickname());
  }

  @Override
  public Response getMember(final Long id) {
    Member member = memberMapper.findMemberById(id);
    return toResponse(member);
  }

  private Response toResponse(Member member) {
    return new Response(member);
  }

  @Transactional
  @Override
  public Response updateMember(final Long id, Update dto) {
    Member existingMember = memberMapper.findMemberById(id);
    memberMapper.checkNicknameDuplicated(dto.getNickname());
    dto.update(existingMember);
    return getMember(id);
  }

  @Transactional
  @Override
  public void leaveMember(final Long id) {
    Member member = memberMapper.findMemberById(id);
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
