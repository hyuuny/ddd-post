package com.setge.dddpost.domain.comment.application;

import static com.setge.dddpost.Fixtures.anComment;
import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.Fixtures.anPost;
import static org.assertj.core.api.Assertions.assertThat;

import com.setge.dddpost.Fixtures;
import com.setge.dddpost.domain.comment.application.CommentDto.Create;
import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentDto.Update;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.post.application.PostService;
import com.setge.dddpost.domain.post.domain.PostRepository;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class CommentServiceImplTest {

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

  @Autowired
  private EntityManager em;

  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    commentRepository.deleteAll();
    postRepository.deleteAll();
    memberRepository.deleteAll();
  }

  @Test
  @DisplayName("댓글 등록")
  void createComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();

    Create create = anComment(userId).build();

    // whenD
    Response comment = commentService.createComment(postId, create);

    // thenD
    assertThat(comment.getPostId()).isEqualTo(postId);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getNickname()).isEqualTo(join.getNickname());
    assertThat(comment.getContent()).isEqualTo(create.getContent());
    assertThat(comment.getNestedComments().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("댓글 조회")
  void getComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();

    Create create = anComment(userId).build();
    Long commentId = commentService.createComment(postId, create).getId();

    // when
    Response comment = commentService.getComment(commentId);

    // then
    assertThat(comment.getId()).isEqualTo(commentId);
    assertThat(comment.getPostId()).isEqualTo(postId);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getNickname()).isEqualTo(join.getNickname());
    assertThat(comment.getContent()).isEqualTo(create.getContent());
    assertThat(comment.getNestedComments().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("댓글 수정")
  void updateComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();

    Create create = anComment(userId).build();
    Long commentId = commentService.createComment(postId, create).getId();

    Update update = Update.builder()
        .content("댓글 수정합니다")
        .build();

    // when
    Response updateComment = commentService.updateComment(commentId, update);

    // then
    assertThat(updateComment.getId()).isEqualTo(commentId);
    assertThat(updateComment.getPostId()).isEqualTo(postId);
    assertThat(updateComment.getUserId()).isEqualTo(userId);
    assertThat(updateComment.getNickname()).isEqualTo(join.getNickname());
    assertThat(updateComment.getContent()).isEqualTo(update.getContent());
    assertThat(updateComment.getNestedComments().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("댓글 삭제")
  void deleteComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();

    Create create = anComment(userId).build();
    Long commentId = commentService.createComment(postId, create).getId();

    // when
    commentService.deleteComment(commentId);

    // then
    commentRepository.findById(commentId).isEmpty();

  }

  @Test
  @DisplayName("댓글 조회")
  void retrieveComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();

    IntStream.rangeClosed(1, 11).forEach(i -> {
      commentService.createComment(postId, anComment(userId).build());
    });

    // when
    DetailedSearchCondition searchCondition = DetailedSearchCondition.builder().build();
    Page<Response> page = commentService
        .retrieveComment(searchCondition, PageRequest.of(0, 10));

    // then
    assertThat(page.getTotalElements()).isEqualTo(11);
    assertThat(page.getTotalPages()).isEqualTo(2);

  }

  private void flushAndClear() {
    em.flush();
    em.clear();
  }


}