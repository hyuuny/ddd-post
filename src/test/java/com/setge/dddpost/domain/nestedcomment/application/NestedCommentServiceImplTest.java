package com.setge.dddpost.domain.nestedcomment.application;

import static com.setge.dddpost.Fixtures.anComment;
import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.Fixtures.anNestedComment;
import static com.setge.dddpost.Fixtures.anPost;
import static org.assertj.core.api.Assertions.assertThat;

import com.setge.dddpost.domain.comment.application.CommentService;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Create;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Response;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Update;
import com.setge.dddpost.domain.nestedcomment.domain.NestedCommentRepository;
import com.setge.dddpost.domain.post.application.PostService;
import com.setge.dddpost.domain.post.domain.PostRepository;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class NestedCommentServiceImplTest {

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

  @Autowired
  private EntityManager em;

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
  void createNestedComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Create create = anNestedComment(userId).build();

    // when
    em.flush();
    em.clear();

    Response nestedComment = nestedCommentService.createNestedComment(commentId, create);

    // then
    assertThat(nestedComment.getUserId()).isEqualTo(userId);
    assertThat(nestedComment.getNickname()).isEqualTo(join.getNickname());
    assertThat(nestedComment.getCommentId()).isEqualTo(commentId);
    assertThat(nestedComment.getContent()).isEqualTo(create.getContent());

  }


  @Test
  @DisplayName("대댓글 상세 조회")
  void getNestedComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Create create = anNestedComment(userId).build();

    // when
    Long id = nestedCommentService.createNestedComment(commentId, create).getId();
    Response nestedComment = nestedCommentService.getNestedComment(id);

    // then
    assertThat(nestedComment.getId()).isEqualTo(id);
    assertThat(nestedComment.getUserId()).isEqualTo(userId);
    assertThat(nestedComment.getNickname()).isEqualTo(join.getNickname());
    assertThat(nestedComment.getCommentId()).isEqualTo(commentId);
    assertThat(nestedComment.getContent()).isEqualTo(create.getContent());

  }

  @Test
  @DisplayName("대댓글 수정")
  void updateNestedComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Create create = anNestedComment(userId).build();
    Long id = nestedCommentService.createNestedComment(commentId, create).getId();

    Update update = Update.builder()
        .content("대댓글 수정!")
        .build();

    // when
    Response updateNestedComment = nestedCommentService.updateNestedComment(id, update);

    // then
    assertThat(updateNestedComment.getId()).isEqualTo(id);
    assertThat(updateNestedComment.getUserId()).isEqualTo(userId);
    assertThat(updateNestedComment.getNickname()).isEqualTo(join.getNickname());
    assertThat(updateNestedComment.getCommentId()).isEqualTo(commentId);
    assertThat(updateNestedComment.getContent()).isEqualTo(update.getContent());

  }

  @Test
  @DisplayName("대댓글 삭제")
  void deleteNestedComment() {

    // given
    Join join = anJoin().build();
    Long userId = memberService.joinMember(join).getId();
    Long postId = postService.createPost(anPost().userId(userId).build()).getId();
    Long commentId = commentService.createComment(postId, anComment(userId).build()).getId();

    Create create = anNestedComment(userId).build();
    Long id = nestedCommentService.createNestedComment(commentId, create).getId();

    // when
    nestedCommentService.deleteNestedComment(id);

    // then
    assertThat(nestedCommentRepository.findById(id)).isEmpty();

  }


}