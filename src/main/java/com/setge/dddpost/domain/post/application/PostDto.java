package com.setge.dddpost.domain.post.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.setge.dddpost.domain.post.domain.Post;
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
import org.springframework.hateoas.server.core.Relation;

public class PostDto {

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "게시물 등록")
  public static class Create {

    @NotEmpty
    @ApiModelProperty(value = "제목", example = "웃긴 자료", required = true, position = 1)
    private String title;

    @NotEmpty
    @ApiModelProperty(value = "내용", example = "6월 13일 있었던 일 ㅋㅋㅋ", required = true, position = 2)
    private String content;

    @NotEmpty
    @ApiModelProperty(value = "게시글 작성자", example = "두덕", required = true, position = 3)
    private String nickname;

    @Default
    @ApiModelProperty(value = "게시물 이미지", position = 4)
    private List<PostImageDto.Create> postImages = Lists.newArrayList();

    public Post toEntity() {
      Post post = Post.builder()
          .title(this.title)
          .content(this.content)
          .nickname(this.nickname)
          .build();

      postImages.stream()
          .map(PostImageDto.Create::toEntity)
          .forEach(post::addImage);

      return post;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ApiModel(value = "게시물 수정")
    public static class Update {

      @NotEmpty
      @ApiModelProperty(value = "제목", example = "웃긴 자료", required = true, position = 1)
      private String title;

      @NotEmpty
      @ApiModelProperty(value = "내용", example = "6월 13일 있었던 일 ㅋㅋㅋ", required = true, position = 2)
      private String content;

      @Default
      @ApiModelProperty(value = "게시물 이미지", position = 3)
      private List<PostImageDto.Create> postImages = Lists.newArrayList();

      public Update(Post entity) {
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Relation(collectionRelation = "posts")
    @JsonInclude(Include.NON_NULL)
    @ApiModel(value = "게시물")
    public static class Response {

      @ApiModelProperty(value = "게시물 ID", example = "1", required = true, position = 1)
      private Long id;

      @ApiModelProperty(value = "제목", example = "웃긴 자료", required = true, position = 2)
      private String title;

      @ApiModelProperty(value = "내용", example = "6월 13일 있었던 일 ㅋㅋㅋ", required = true, position = 3)
      private String content;

      @ApiModelProperty(value = "게시글 작성자", example = "두덕", required = true, position = 4)
      private String nickname;

      @ApiModelProperty(value = "추천 게시물 여부", example = "true", required = true, position = 5)
      private Boolean recommend;

      @ApiModelProperty(value = "게시물 이미지", position = 6)
      private List<PostImageDto.Response> postImages = Lists.newArrayList();

      @ApiModelProperty(value = "등록일시", example = "2020-06-13T21:18:58.139065", required = true, position = 7)
      private LocalDateTime createdAt;

      @ApiModelProperty(value = "최종수정일", example = "2020-06-13T21:18:58.139065", required = true, position = 8)
      private LocalDateTime lastModifiedAt;

      public Response(Post entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.nickname = entity.getNickname();
        this.recommend = entity.isRecommend();
        this.postImages = entity.getPostImages().stream()
            .map(PostImageDto.Response::new)
            .collect(Collectors.toList());
        this.createdAt = entity.getCreatedAt();
        this.lastModifiedAt = entity.getLastModifiedAt();
      }

    }


  }

}