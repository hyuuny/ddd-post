package com.setge.dddpost.domain.nestedcomment.presentation;

import static com.setge.dddpost.Fixtures.anComment;
import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.Fixtures.anNestedComment;
import static com.setge.dddpost.Fixtures.anPost;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.setge.dddpost.BaseIntegrationTest;
import com.setge.dddpost.domain.comment.application.CommentService;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Create;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Update;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentService;
import com.setge.dddpost.domain.nestedcomment.domain.NestedCommentRepository;
import com.setge.dddpost.domain.post.application.PostService;
import com.setge.dddpost.domain.post.domain.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class NestedCommentRestControllerTest extends BaseIntegrationTest {

  private static final String BASE_URL = "/api/nested-comments";

  @Autowired
  private NestedCommentRepository nestedCommentRepository;

  @Autowired
  private NestedCommentService nestedCommentService;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentService commentService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private PostService postService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberService memberService;

  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    nestedCommentRepository.deleteAll();
    commentRepository.deleteAll();
    postRepository.deleteAll();
    memberRepository.deleteAll();
  }

  @Test
  @DisplayName("대댓글 등록")
  void createNestedComment() throws Exception {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Create create = anNestedComment(userId).build();

    // when
    ResultActions resultActions = mockMvc.perform(
        post(BASE_URL + "/{id}/register/nested", commentId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(objectMapper.writeValueAsString(create)))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists());

  }

  @Test
  @DisplayName("대댓글 상세 조회")
  void getNestedComment() throws Exception {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Long nestedCommentId = nestedCommentService
        .createNestedComment(commentId, anNestedComment(userId).build()).getId();

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{id}", nestedCommentId)
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
  @DisplayName("대댓글 수정")
  void updateNestedComment() throws Exception{

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Long nestedCommentId = nestedCommentService
        .createNestedComment(commentId, anNestedComment(userId).build()).getId();

    Update update = Update.builder()
        .content("대댓글 수정!")
        .build();

    // when
    ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/{id}", nestedCommentId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(objectMapper.writeValueAsString(update)))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists());

  }

  @Test
  @DisplayName("대댓글 삭제")
  void deleteNestedComment() throws Exception {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Long nestedCommentId = nestedCommentService
        .createNestedComment(commentId, anNestedComment(userId).build()).getId();

    // when
    ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{id}", nestedCommentId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isNoContent());

  }


}