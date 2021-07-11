package com.setge.dddpost.domain.member.presentation;

import static com.setge.dddpost.Fixtures.anJoin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.setge.dddpost.BaseIntegrationTest;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class MemberRestControllerTest extends BaseIntegrationTest {

  private static final String BASE_URL = "/api/members";

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
  @DisplayName("회원 가입")
  void joinMember() throws Exception {

    // given
    Join join = anJoin().build();

    // when
    ResultActions resultActions = mockMvc.perform(post(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(objectMapper.writeValueAsString(join)))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists());

  }

  @Test
  @DisplayName("회원 상세 조회")
  void getMember() throws Exception {

    // given
    Join join = anJoin().build();
    Long joinId = memberService.joinMember(join).getId();

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{id}", joinId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists());

  }

  @Test
  @DisplayName("회원 수정")
  void updateMember() throws Exception{

    // given
    Join join = anJoin().build();
    Long joinId = memberService.joinMember(join).getId();

    Update update = Update.builder()
        .password("56785678")
        .nickname("수정했어요")
        .build();

    // when
    ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/{id}", joinId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(objectMapper.writeValueAsString(update)))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.nickname").value(update.getNickname()));

  }

  @Test
  @DisplayName("회원 탈퇴")
  void leaveMember() throws Exception {

    // given
    Join join = anJoin().build();
    Long joinId = memberService.joinMember(join).getId();

    // when
    ResultActions resultActions = mockMvc.perform(post(BASE_URL + "/leave/{id}", joinId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isNoContent());

  }


}