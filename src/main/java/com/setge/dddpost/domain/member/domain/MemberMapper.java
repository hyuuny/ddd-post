package com.setge.dddpost.domain.member.domain;

public interface MemberMapper {

  Member findMemberById(final Long id);

  void checkEmailDuplicated(final String email);

  void checkNicknameDuplicated(final String nickname);

  void checkEmailAndNicknameDuplicated(final String email, final String nickname);

}
