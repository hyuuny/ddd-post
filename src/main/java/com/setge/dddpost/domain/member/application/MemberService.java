package com.setge.dddpost.domain.member.application;

import com.setge.dddpost.domain.member.application.MemberDto.CheckEmail;
import com.setge.dddpost.domain.member.application.MemberDto.CheckNickname;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

  Response joinMember(Join dto);

  void checkEmail(CheckEmail dto);

  void checkNickname(CheckNickname dto);

  Response getMember(final Long id);

  Response updateMember(final Long id, Update dto);

  void leaveMember(final Long id);

  Page<Response> findAllMembers(Pageable pageable);

  Page<Response> retrieveMember(DetailedSearchCondition searchCondition, Pageable pageable);

}
