package com.setge.dddpost.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.setge.dddpost.domain.member.application.MemberDto.CheckEmail;
import com.setge.dddpost.domain.member.application.MemberDto.CheckNickname;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.member.domain.Member.MemberStatus;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class MemberServiceImplTest {

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;


  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    memberRepository.deleteAll();
  }


  @Test
  @DisplayName("회원기입")
  void joinMember() {

    // given
    MemberDto.Join join = getJoin();

    // when
    Response result = memberService.joinMember(join);

    // then
    assertThat(result.getEmail()).isEqualTo(join.getEmail());
    assertThat(result.getNickname()).isEqualTo(join.getNickname());
    assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVATION.toString());

  }

  private Join getJoin() {
    return Join.builder()
        .email("exam@naver.com")
        .password("12341234")
        .nickname("두덕")
        .build();
  }

  @Test
  @DisplayName("이메일 중복 예외")
  void checkEmail() throws Exception {

    // given
    MemberDto.Join join = getJoin();
    Response result = memberService.joinMember(join);

    // when
    CheckEmail checkEmail = CheckEmail.builder()
        .email("exam@naver.com")
        .build();

    // then
    HttpStatusMessageException assertThrows = assertThrows(
        HttpStatusMessageException.class, () -> memberService.checkEmail(checkEmail));
    assertEquals("member.email.duplicated", assertThrows.getMessage());
  }

  @Test
  @DisplayName("닉네임 중복 예외")
  void checkNickname() throws Exception {

    // given
    MemberDto.Join join = getJoin();
    memberService.joinMember(join);

    // when
    CheckNickname checkNickname = CheckNickname.builder()
        .nickname("두덕")
        .build();

    // then
    HttpStatusMessageException assertThrows = assertThrows(HttpStatusMessageException.class,
        () -> memberService.checkNickname(checkNickname));
    assertEquals("member.nickname.duplicated", assertThrows.getMessage());

  }

  @Test
  @DisplayName("회원 조회")
  void getMember() {

    // given
    MemberDto.Join join = getJoin();
    Long memberId = memberService.joinMember(join).getId();

    // when
    Response result = memberService.getMember(memberId);

    // then
    assertThat(result.getEmail()).isEqualTo(join.getEmail());
    assertThat(result.getNickname()).isEqualTo(join.getNickname());
    assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVATION.toString());

  }

  @Test
  @DisplayName("회원 수정")
  void updateMember() {

    // given
    MemberDto.Join join = getJoin();
    Long memberId = memberService.joinMember(join).getId();

    Update update = Update.builder()
        .password("12345678")
        .nickname("닉네임 수정")
        .build();

    // when
    Response result = memberService.updateMember(memberId, update);

    // then
    assertThat(result.getEmail()).isEqualTo(join.getEmail());
    assertThat(result.getNickname()).isEqualTo(update.getNickname());
    assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVATION.toString());

  }

  @Test
  @DisplayName("회원 탈퇴")
  void leaveMember() {

    // given
    MemberDto.Join join = getJoin();
    Long memberId = memberService.joinMember(join).getId();

    // when
    memberService.leaveMember(memberId);
    Response result = memberService.getMember(memberId);

    // then
    assertThat(result.getStatus()).isEqualTo(MemberStatus.DISABLE.toString());

  }

  @Test
  @DisplayName("회원 검색")
  void retrieveMember() {

    // given
    IntStream.rangeClosed(1, 31).forEach(i -> {

      Join join = Join.builder()
          .email("exam@naver.com" + i)
          .password("12341234")
          .nickname("가입했어요 " + i)
          .build();
      Long id = memberService.joinMember(join).getId();

      if (i % 3 == 0) {
        memberService.leaveMember(id);
      }
    });

    // when
    DetailedSearchCondition searchCondition = DetailedSearchCondition.builder()
//        .searchOption("nickname")
//        .keyword("3")
        .status(MemberStatus.DISABLE)
        .build();

    Page<Response> members = memberService.retrieveMember(searchCondition, PageRequest.of(0, 10));

    // then
    memberPrint(members);

  }

  private void memberPrint(Page<Response> members) {
    for (Response member : members) {
      System.out.println("id : " + member.getId());
      System.out.println("Email : " + member.getEmail());
      System.out.println("Nickname : " + member.getNickname());
      System.out.println("status : " + member.getStatus());
      System.out.println("==========다음 회원==========");
    }
    System.out.println("총 요소 : " + members.getTotalElements());
    System.out.println("총 페이지 : " + members.getTotalPages());
  }
}