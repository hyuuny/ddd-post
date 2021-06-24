package com.setge.dddpost.domain.member.domain;

import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberValidatorImpl implements MemberValidator {

  private final MemberRepository memberRepository;


  @Override
  public void validate(Member member) {
    validation(member);
  }

  private void validation(Member member) {

    if (emailValidation(member.getEmail())) {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.email.duplicated");
    }

    if (nicknameValidation(member.getNickname())) {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.nickname.duplicated");
    }

  }


  public boolean emailValidation(final String email) {
    return memberRepository.findByEmail(email).isPresent();
  }

  public boolean nicknameValidation(final String nickname) {
    return memberRepository.findByNickname(nickname).isPresent();
  }


}
