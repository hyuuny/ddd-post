package com.setge.dddpost.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import com.setge.dddpost.domain.post.application.PostDto.ChangeRecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.RecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import com.setge.dddpost.domain.post.domain.PostRepository;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class PostServiceImplTest {

  @Autowired
  private PostService postService;

  @Autowired
  private PostRepository postRepository;

  @AfterEach
  void tearDown() {
    System.out.println("delete All");
    postRepository.deleteAll();
  }

  @Test
  @DisplayName("게시물 등록")
  void createPost() {

    // given
    List<PostImageDto.Create> createImages = getCreateImage();
    Create create = getCreatePost(createImages);

    // when
    Response post = postService.createPost(create);

    // then
    assertThat(post.getType()).isEqualTo(create.getType().toString());
    assertThat(post.getTitle()).isEqualTo(create.getTitle());
    assertThat(post.getContent()).isEqualTo(create.getContent());
    assertThat(post.getNickname()).isEqualTo(create.getNickname());
    assertThat(post.getPostImages().size()).isEqualTo(create.getPostImages().size());

  }

  private Create getCreatePost(List<PostImageDto.Create> createImages) {
    return Create.builder()
        .type(PostType.FUNNY)
        .title("재미있는 자료 올립니다 ㅋㅋㅋ")
        .content("6월 13일 있었던 일 ㅋㅋㅋ")
        .nickname("두덕")
        .postImages(createImages)
        .build();
  }

  private List<PostImageDto.Create> getCreateImage() {

    PostImageDto.Create createImage1 = PostImageDto.Create.builder()
        .imageUrl("https://exam-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1315_100.jpg")
        .priority(1)
        .build();

    PostImageDto.Create createImage2 = PostImageDto.Create.builder()
        .imageUrl("https://exam-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1315_300.jpg")
        .priority(3)
        .build();

    PostImageDto.Create createImage3 = PostImageDto.Create.builder()
        .imageUrl("https://exam-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1315_200.jpg")
        .priority(2)
        .build();

    PostImageDto.Create createImage4 = PostImageDto.Create.builder()
        .imageUrl("https://exam-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1315_400.jpg")
        .priority(4)
        .build();

    List<PostImageDto.Create> createList = Lists.newArrayList();
    createList.add(createImage1);
    createList.add(createImage2);
    createList.add(createImage3);
    createList.add(createImage4);

    return createList;
  }

  @Test
  @DisplayName("게시물 조회")
  void getPost() {

    // given
    List<PostImageDto.Create> createImages = getCreateImage();
    Create create = getCreatePost(createImages);

    // when
    Long postId = postService.createPost(create).getId();
    Response post = postService.getPost(postId);

    // then
    assertThat(post.getId()).isEqualTo(postId);
    assertThat(post.getType()).isEqualTo(create.getType().toString());
    assertThat(post.getTitle()).isEqualTo(create.getTitle());
    assertThat(post.getContent()).isEqualTo(create.getContent());
    assertThat(post.getNickname()).isEqualTo(create.getNickname());
    assertThat(post.getPostImages().size()).isEqualTo(create.getPostImages().size());

  }

  @Test
  @DisplayName("게시물 수정")
  void updatePost() {

    // given
    List<PostImageDto.Create> createImages = getCreateImage();
    Create create = getCreatePost(createImages);
    Long postId = postService.createPost(create).getId();

    // when
    PostDto.Update update = Update.builder()
        .type(PostType.HORROR)
        .title("수정 제목")
        .content("수정 내용")
        .postImages(
            Lists.newArrayList(PostImageDto.Create.builder()
                .imageUrl("modi.jpg")
                .priority(11)
                .build()))
        .build();

    Response updatePost = postService.updatePost(postId, update);

    // then
    assertThat(updatePost.getId()).isEqualTo(postId);
    assertThat(updatePost.getType()).isEqualTo(update.getType().toString());
    assertThat(updatePost.getTitle()).isEqualTo(update.getTitle());
    assertThat(updatePost.getContent()).isEqualTo(update.getContent());
    assertThat(updatePost.getPostImages().size()).isEqualTo(update.getPostImages().size());

  }

  @Test
  @DisplayName("게시물 삭제")
  void deletePost() {

    // given
    List<PostImageDto.Create> createImages = getCreateImage();
    Create create = getCreatePost(createImages);
    Long postId = postService.createPost(create).getId();

    // when
    postService.deletePost(postId);

    // then
    assertThat(postRepository.findById(postId)).isEmpty();

  }

  @Test
  @DisplayName("추천 게시물 선정 or 변경")
  void changeRecommendPost() {

    // given
    List<RecommendPost> recommendPosts = Lists.newArrayList();

    IntStream.rangeClosed(1, 5).forEach(i -> {
      Create create = Create.builder()
          .type(PostType.FUNNY)
          .title("재미있는 자료 올립니다 ㅋㅋㅋ")
          .content("6월 13일 있었던 일 ㅋㅋㅋ")
          .nickname("두덕")
          .postImages(
              Lists.newArrayList(PostImageDto.Create.builder()
                  .imageUrl("image " + i)
                  .priority(i)
                  .build()))
          .build();

      Long id = postService.createPost(create).getId();

      if (i % 2 == 0) {
        recommendPosts.add(RecommendPost.builder().id(id).recommend(true).build());
      }
    });

    // when
    ChangeRecommendPost changeRecommendPost = ChangeRecommendPost.builder()
        .recommendPosts(recommendPosts)
        .build();

    postService.changeRecommendPost(changeRecommendPost);

    // then
    for (RecommendPost result : recommendPosts) {
      assertThat(result.getRecommend()).isTrue();
    }

  }

  @Test
  @DisplayName("게시물 검색")
  void retrievePost() {

    // given
    List<PostImageDto.Create> createImages = getCreatePostImages();

    IntStream.rangeClosed(1, 21).forEach(i -> {
      Create create = Create.builder()
          .type(PostType.FUNNY)
          .title("웃긴 자료 " + i)
          .content("재밌었던 일 " + i + "번째 !")
          .nickname("두덕 " + i)
          .postImages(createImages)
          .build();
      postService.createPost(create);
    });

    // when
    DetailedSearchCondition searchCondition = DetailedSearchCondition.builder()
        .searchOption("title")
        .keyword("2")
        .build();

    Page<Response> responses = postService.retrievePost(searchCondition, PageRequest.of(0, 10));

    // then
    for (Response response : responses) {
      System.out.println("id : " + response.getId());
      System.out.println("type : " + response.getType());
      System.out.println("title : " + response.getTitle());
      System.out.println("content : " + response.getContent());
      System.out.println("nickname : " + response.getNickname());

      for (PostImageDto.Response image : response.getPostImages()) {
        System.out.println("image id : " + image.getId());
        System.out.println("image post id : " + image.getPostId());
        System.out.println("image url : " + image.getImageUrl());
        System.out.println("image priority : " + image.getPriority());
      }
      System.out.println();
      System.out.println("다음 게시물");
      System.out.println();
    }

    System.out.println("total Page : " + responses.getTotalPages());
    System.out.println("total Elements : " + responses.getTotalElements());

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