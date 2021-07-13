package com.setge.dddpost.domain.member.domain;

import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberSearchDto;
import com.setge.dddpost.domain.member.infrastructure.MemberQueryRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberDomainService {

  private final MemberRepository memberRepository;
  private final MemberQueryRepository memberQueryRepository;


  public void emailCheck(String email) {
    memberRepository.findByEmail(email).ifPresent(member -> {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.email.duplicated");
    });
  }

  public void nicknameCheck(String nickname) {
    memberRepository.findByNickname(nickname).ifPresent(member -> {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.nickname.duplicated");
    });
  }

  public Member getMember(Long id) {
    return memberRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.notFound", id));
  }

  public Page<MemberSearchDto> getMembers(Pageable pageable) {
    return memberQueryRepository.findAllMembers(pageable);
  }

  public Page<MemberSearchDto> search(DetailedSearchCondition searchCondition, Pageable pageable) {
    return memberQueryRepository.search(searchCondition, pageable);
  }


}
