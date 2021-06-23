package com.setge.dddpost.domain.member.domain;

import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberMapperImpl implements MemberMapper {

  private final MemberRepository memberRepository;

  @Override
  public Member findMemberById(Long id) {
    return memberRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.notFound", id));
  }

  @Override
  public void checkEmailDuplicated(final String email) {
    memberRepository.findByEmail(email).ifPresent(member -> {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.email.duplicated");
    });
  }

  @Override
  public void checkNicknameDuplicated(final String nickname) {
    memberRepository.findByNickname(nickname).ifPresent(member -> {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.nickname.duplicated");
    });
  }

  @Override
  public void checkEmailAndNicknameDuplicated(final String email, final String nickname) {
    checkEmailDuplicated(email);
    checkEmailDuplicated(nickname);
  }


}
