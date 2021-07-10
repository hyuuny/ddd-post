package com.setge.dddpost.domain.nestedcomment.presentation;

import static com.setge.dddpost.Fixtures.anComment;
import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.Fixtures.anNestedComment;
import static com.setge.dddpost.Fixtures.anPost;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.setge.dddpost.BaseIntegrationTest;
import com.setge.dddpost.domain.comment.application.CommentService;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Create;
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

class NestedCommentAdminRestControllerTest extends BaseIntegrationTest {

  private static final String BASE_URL = "/admin/api/nested-comments";

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
  @DisplayName("대댓글 삭제")
  void deleteComment() throws Exception {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();
    Create create = anNestedComment(userId).build();
    Long nestedCommentId = nestedCommentService.createNestedComment(commentId, create).getId();

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