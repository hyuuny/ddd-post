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
  @DisplayName("νμκΈ°μ")
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
        .nickname("λλ")
        .build();
  }

  @Test
  @DisplayName("μ΄λ©μΌ μ€λ³΅ μμΈ")
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
  @DisplayName("λλ€μ μ€λ³΅ μμΈ")
  void checkNickname() throws Exception {

    // given
    MemberDto.Join join = getJoin();
    memberService.joinMember(join);

    // when
    CheckNickname checkNickname = CheckNickname.builder()
        .nickname("λλ")
        .build();

    // then
    HttpStatusMessageException assertThrows = assertThrows(HttpStatusMessageException.class,
        () -> memberService.checkNickname(checkNickname));
    assertEquals("member.nickname.duplicated", assertThrows.getMessage());

  }

  @Test
  @DisplayName("νμ μ‘°ν")
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
  @DisplayName("νμ μμ ")
  void updateMember() {

    // given
    MemberDto.Join join = getJoin();
    Long memberId = memberService.joinMember(join).getId();

    Update update = Update.builder()
        .password("12345678")
        .nickname("λλ€μ μμ ")
        .build();

    // when
    Response result = memberService.updateMember(memberId, update);

    // then
    assertThat(result.getEmail()).isEqualTo(join.getEmail());
    assertThat(result.getNickname()).isEqualTo(update.getNickname());
    assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVATION.toString());

  }

  @Test
  @DisplayName("νμ νν΄")
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
  @DisplayName("νμ κ²μ")
  void retrieveMember() {

    // given
    IntStream.rangeClosed(1, 31).forEach(i -> {

      Join join = Join.builder()
          .email("exam@naver.com" + i)
          .password("12341234")
          .nickname("κ°μνμ΄μ " + i)
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
      System.out.println("==========λ€μ νμ==========");
    }
    System.out.println("μ΄ μμ : " + members.getTotalElements());
    System.out.println("μ΄ νμ΄μ§ : " + members.getTotalPages());
  }
}