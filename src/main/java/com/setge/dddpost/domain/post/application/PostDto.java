package com.setge.dddpost.domain.post.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class PostDto {

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "게시물 등록")
  public static class Create {

    @NotEmpty
    @ApiModelProperty(value = "게시물 타입", example = "웃긴", required = true, position = 1)
    private PostType type;

    @NotEmpty
    @ApiModelProperty(value = "제목", example = "웃긴 자료", required = true, position = 2)
    private String title;

    @ApiModelProperty(value = "내용", example = "6월 13일 있었던 일 ㅋㅋㅋ", required = false, position = 3)
    private String content;

    @NotEmpty
    @ApiModelProperty(value = "게시글 작성자", example = "두덕", required = true, position = 4)
    private String nickname;

    @Default
    @ApiModelProperty(value = "게시물 이미지", position = 5)
    private List<PostImageDto.Create> postImages = Lists.newArrayList();

    public Post toEntity() {
      Post post = Post.builder()
          .type(this.type)
          .title(this.title)
          .content(this.content)
          .nickname(this.nickname)
          .build();

      this.postImages.stream()
          .map(PostImageDto.Create::toEntity)
          .forEach(post::addImage);

      return post;
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "게시물 수정")
  public static class Update {

    @NotEmpty
    @ApiModelProperty(value = "게시물 타입", example = "웃긴", required = true, position = 1)
    private PostType type;

    @NotEmpty
    @ApiModelProperty(value = "제목", example = "웃긴 자료", required = true, position = 2)
    private String title;

    @ApiModelProperty(value = "내용", example = "6월 13일 있었던 일 ㅋㅋㅋ", required = false, position = 3)
    private String content;

    @Default
    @ApiModelProperty(value = "게시물 이미지", position = 4)
    private List<PostImageDto.Create> postImages = Lists.newArrayList();

    public void Update(Post entity) {
      entity.changeType(this.type);
      entity.changeTitle(this.title);
      entity.changeContent(this.content);

      entity.getPostImages().clear();
      postImages.stream()
          .map(PostImageDto.Create::toEntity)
          .forEach(entity::addImage);
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "추천 게시물 선정")
  public static class RecommendPost {

    @NotNull
    @ApiModelProperty(value = "게시물 ID", example = "1", required = true, position = 1)
    private Long id;

    @NotNull
    @ApiModelProperty(value = "추천게시물 선정여부", example = "ture", required = true, position = 2)
    private Boolean recommend;

  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "추천 게시물 변경")
  public static class ChangeRecommendPost {

    @Default
    @ApiModelProperty(value = "추천 게시물들", example = "1", required = true, position = 1)
    private List<RecommendPost> recommendPosts = Lists.newArrayList();

  }


  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "posts")
  @JsonInclude(Include.NON_NULL)
  @ApiModel(value = "게시물")
  public static class Response {

    @ApiModelProperty(value = "게시물 ID", example = "1", required = true, position = 1)
    private Long id;

    @ApiModelProperty(value = "게시물 타입", example = "웃긴", required = true, position = 2)
    private String type;

    @ApiModelProperty(value = "제목", example = "웃긴 자료", required = true, position = 4)
    private String title;

    @ApiModelProperty(value = "내용", example = "6월 13일 있었던 일 ㅋㅋㅋ", required = true, position = 5)
    private String content;

    @ApiModelProperty(value = "게시글 작성자", example = "두덕", required = true, position = 6)
    private String nickname;

    @ApiModelProperty(value = "추천 게시물 여부", example = "true", required = true, position = 7)
    private Boolean recommend;

    @ApiModelProperty(value = "게시물 이미지", position = 8)
    private List<PostImageDto.Response> postImages = Lists.newArrayList();

    @ApiModelProperty(value = "등록일시", example = "2020-06-13T21:18:58.139065", required = true, position = 9)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "최종수정일", example = "2020-06-13T21:18:58.139065", required = true, position = 10)
    private LocalDateTime lastModifiedAt;

    public Response(Post entity) {
      this.id = entity.getId();
      this.type = entity.toType();
      this.title = entity.getTitle();
      this.content = entity.getContent();
      this.nickname = entity.getNickname();
      this.recommend = entity.isRecommend();
      this.postImages = entity.getPostImages().stream()
          .map(PostImageDto.Response::new)
          .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
          .collect(Collectors.toList());
      this.createdAt = entity.getCreatedAt();
      this.lastModifiedAt = entity.getLastModifiedAt();
    }

  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "게시물 상세검색조건")
  public static class DetailedSearchCondition {

    @ApiModelProperty(value = "검색 옵션", example = "email", required = false, position = 1)
    private String searchOption;

    @ApiModelProperty(value = "검색 키워드", example = "웃긴", required = false, position = 2)
    private String keyword;

    @ApiModelProperty(value = "베스트리뷰", example = "true", required = false, position = 3)
    private Boolean recommend;

  }

}
