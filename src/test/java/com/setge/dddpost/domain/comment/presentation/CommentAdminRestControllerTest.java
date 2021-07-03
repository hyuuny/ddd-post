package com.setge.dddpost.domain.comment.presentation;

import static com.setge.dddpost.Fixtures.anComment;
import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.Fixtures.anPost;
import static com.setge.dddpost.Fixtures.anPostImage;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.setge.dddpost.BaseIntegrationTest;
import com.setge.dddpost.domain.comment.application.CommentDto.Create;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentService;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.post.application.PostDto;
import com.setge.dddpost.domain.post.application.PostService;
import com.setge.dddpost.domain.post.domain.PostRepository;
import java.util.stream.IntStream;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;


class CommentAdminRestControllerTest extends BaseIntegrationTest {

  private static final String BASE_URL = "/admin/api/comments";

  @Autowired
  private CommentService commentService;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostService postService;

  @Autowired
  private PostRepository postRepository;

  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    commentRepository.deleteAll();
    memberRepository.deleteAll();
    postRepository.deleteAll();
  }

  @Test
  @DisplayName("댓글 검색")
  void retrieveComment() throws Exception {

    // given
    Join join = anJoin().build();
    Long memberId = memberService.joinMember(join).getId();

    PostDto.Create createPost = anPost()
        .userId(memberId)
        .postImages(Lists.newArrayList(anPostImage().priority(1).build()))
        .build();
    Long postId = postService.createPost(createPost).getId();

    IntStream.rangeClosed(0, 10).forEach(i -> {

      Create createComment = anComment(memberId).build();
      commentService.createComment(postId, createComment);

    });

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
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
  @DisplayName("댓글 상세 조회")
  void getComment() throws Exception {

    // given
    Long commentId = createCommentResponse().getId();

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{id}", commentId)
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
  @DisplayName("댓글 삭제")
  void deleteComment() throws Exception {

    // given
    Long commentId = createCommentResponse().getId();

    // when
    ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{id}", commentId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isNoContent());

  }


  private Response createCommentResponse() {
    Join join = anJoin().build();
    Long memberId = memberService.joinMember(join).getId();

    PostDto.Create createPost = anPost()
        .userId(memberId)
        .postImages(Lists.newArrayList(anPostImage().priority(1).build()))
        .build();
    Long postId = postService.createPost(createPost).getId();

    Create createComment = anComment(memberId).build();
    return commentService.createComment(postId, createComment);
  }

}