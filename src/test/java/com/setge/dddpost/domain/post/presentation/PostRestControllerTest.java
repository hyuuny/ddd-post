package com.setge.dddpost.domain.post.presentation;

import static com.setge.dddpost.Fixtures.anComment;
import static com.setge.dddpost.Fixtures.anJoin;
import static com.setge.dddpost.Fixtures.anNestedComment;
import static com.setge.dddpost.Fixtures.anPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.setge.dddpost.BaseIntegrationTest;
import com.setge.dddpost.Fixtures;
import com.setge.dddpost.domain.comment.application.CommentDto;
import com.setge.dddpost.domain.comment.application.CommentService;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.member.domain.MemberRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentService;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.application.PostImageDto;
import com.setge.dddpost.domain.post.application.PostService;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import com.setge.dddpost.domain.post.domain.PostRepository;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class PostRestControllerTest extends BaseIntegrationTest {

  private static final String BASE_URL = "/api/posts";

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private PostService postService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberService memberService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private NestedCommentService nestedCommentService;


  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    postRepository.deleteAll();
    memberRepository.deleteAll();
  }


  @Test
  @DisplayName("게시물에 달린 댓글 + 대댓글 조회")
  public void getPostAndCommentAndNestedComment() throws Exception {

    // given
    Join join = anJoin()
        .nickname("펭귄")
        .build();
    Long joinId = memberService.joinMember(join).getId();

    Create create = anPost()
        .userId(joinId)
        .postImages(getCreatePostImages())
        .build();

    Long postId = postService.createPost(create).getId();

    createCommentAndNestedComment(joinId, postId);
    postService.getPost(postId);

    // then
    ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{id}", postId)
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
  @DisplayName("게시물 조회")
  void retrievePost() throws Exception {

    // given

    Join join = Fixtures.anJoin()
        .nickname("펭귄")
        .build();
    Long joinId = memberService.joinMember(join).getId();
    List<PostImageDto.Create> createPostImages = getCreatePostImages();

    IntStream.rangeClosed(1, 31).forEach(i -> {
      Create create;

      if (i % 2 == 0) {
        create = Create.builder()
            .type(PostType.FREEDOM)
            .userId(joinId)
            .title("여행 기록! " + i)
            .content(i + "일 여행 다녀왔어요~")
            .postImages(createPostImages)
            .build();
      } else {
        create = Create.builder()
            .type(PostType.FUNNY)
            .userId(joinId)
            .title(i + "일 실화 ")
            .content("제게 있었던 " + i + "일 실화입니다 ㅋㅋㅋ")
            .build();
      }
      Long postId = postService.createPost(create).getId();
      createCommentAndNestedComment(joinId, postId);

    });

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE)
//        .param("searchOption", "title")
//        .param("keyword", "기록")
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
  @DisplayName("게시물 등록")
  void createPost() throws Exception {

    // given
    Join join = Fixtures.anJoin()
        .nickname("펭귄")
        .build();
    Long joinId = memberService.joinMember(join).getId();

    Create create = Fixtures.anPost()
        .userId(joinId)
        .postImages(getCreatePostImages())
        .build();

    // when
    ResultActions resultActions = mockMvc.perform(post(BASE_URL)
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
  @DisplayName("게시물 상세 조회")
  void getPost() throws Exception {

    Join join = Fixtures.anJoin()
        .nickname("펭귄")
        .build();
    Long joinId = memberService.joinMember(join).getId();

    Create create = Fixtures.anPost()
        .userId(joinId)
        .postImages(getCreatePostImages())
        .build();

    Long id = postService.createPost(create).getId();

    // when
    ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{id}", id)
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
  @DisplayName("게시물 수정")
  void updatePost() throws Exception {

    // given
    Join join = Fixtures.anJoin()
        .nickname("펭귄")
        .build();
    Long joinId = memberService.joinMember(join).getId();

    Create create = Fixtures.anPost()
        .userId(joinId)
        .postImages(getCreatePostImages())
        .build();

    Long postId = postService.createPost(create).getId();

    Update update = Update.builder()
        .type(PostType.HORROR)
        .title("늦게 퇴근하던 중에 생긴 일")
        .content("제가 잘 못 본것 일까요..? 사진도 같이 올려요...")
        .postImages(
            Lists.newArrayList(
                Lists.newArrayList(
                    PostImageDto.Create.builder().imageUrl("horror1.jpg").priority(1).build())
            )
        )
        .build();

    // when
    ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/{id}", postId)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(objectMapper.writeValueAsString(update)))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.links").exists())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.type").value(update.getType().toString()))
        .andExpect(jsonPath("$.title").value(update.getTitle()))
        .andExpect(jsonPath("$.content").value(update.getContent()));

  }

  @Test
  @DisplayName("게시물 삭제")
  void deletePost() throws Exception {

    // given
    Join join = Fixtures.anJoin()
        .nickname("펭귄")
        .build();
    Long joinId = memberService.joinMember(join).getId();

    Create create = Fixtures.anPost()
        .userId(joinId)
        .postImages(getCreatePostImages())
        .build();

    Long id = postService.createPost(create).getId();

    // when
    ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/{id}", id)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print());

    // then
    resultActions
        .andExpect(status().isNoContent());

  }

  private void createCommentAndNestedComment(Long joinId, Long postId) {
    IntStream.rangeClosed(0, 5).forEach(i -> {
      Long commentId = commentService
          .createComment(postId, anComment(joinId).content("댓글 " + i).build()).getId();

      IntStream.rangeClosed(0, 3).forEach(j -> {
        nestedCommentService
            .createNestedComment(commentId, anNestedComment(joinId).content("대댓글 " + j).build());
      });

    });
  }


  private List<PostImageDto.Create> getCreatePostImages() {

    List<PostImageDto.Create> createImages = Lists.newArrayList();

    IntStream.rangeClosed(1, 11).parallel().forEach(i -> {
      PostImageDto.Create create = PostImageDto.Create.builder()
          .imageUrl("url " + i)
          .priority(i)
          .build();
      createImages.add(create);
    });
    return createImages;
  }

}