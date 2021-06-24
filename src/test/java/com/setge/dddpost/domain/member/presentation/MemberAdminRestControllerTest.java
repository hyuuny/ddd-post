package com.setge.dddpost.domain.member.presentation;

import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.domain.member.domain.Member.MemberStatus.DISABLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.setge.dddpost.Fixtures;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.Member.MemberStatus;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberAdminRestControllerTest {

  private static final String BASE_URL = "/admin/api/members";
  private MockMvc mockMvc;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext ctx;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))
        .alwaysDo(print()).build();
  }

  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    memberRepository.deleteAll();
  }

  @Test
  @DisplayName("회원 조회")
  void retrieveMember() throws Exception {

    // given

    IntStream.range(1, 31).forEach(i -> {
      Join join;

      if (i % 2 == 0) {
        join = Join.builder()
            .email("exam" + i + "@naver.com")
            .password("12341234")
            .nickname("두덕" + i)
            .build();
      } else {
        join = Join.builder()
            .email("exam" + i + "@naver.com")
            .password("56785678")
            .nickname("개방" + i)
            .build();
      }
      Long id = memberService.joinMember(join).getId();

      if (i % 2 == 0) {
        memberService.leaveMember(id);
      }

    });

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE)
//        .param("searchOption", "nickname")
//        .param("keyword", "개방")
            .param("status", String.valueOf(DISABLE))
    )
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page.size").exists())
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalPages").exists())
        .andExpect(jsonPath("$.page.number").exists());

  }

  @Test
  @DisplayName("회원 상세 조회")
  void getMember() throws Exception {

    // given
    Join join = anJoin().build();

    Long memberId = memberService.joinMember(join).getId();

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{id}", memberId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists());

  }


}