package com.setge.dddpost.domain.post.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.setge.dddpost.domain.post.domain.PostImage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class PostImageDto {

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "게시물 이미지 등록")
  public static class Create {

    @NotEmpty
    @ApiModelProperty(value = "이미지 url", example = "https://exam-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1315_100.jpg", required = true, position = 1)
    private String imageUrl;

    @ApiModelProperty(value = "우선순위", example = "1", required = false, position = 2)
    private Integer priority;

    public PostImage toEntity() {
      return PostImage.builder()
          .imageUrl(this.imageUrl)
          .priority(this.priority)
          .build();
    }

  }

  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(value = "게시물 이미지")
  @Relation(collectionRelation = "postImages")
  @JsonInclude(Include.NON_NULL)
  public static class Response {

    @ApiModelProperty(value = "게시물 이미지 ID", required = true, position = 1)
    private Long id;

    @ApiModelProperty(value = "게시물 ID", required = true, position = 2)
    private Long postId;

    @ApiModelProperty(value = "이미지 url", required = true, position = 3)
    private String imageUrl;

    @ApiModelProperty(value = "우선순위", required = false, position = 4)
    private Integer priority;

    public Response(PostImage entity) {
      this.id = entity.getId();
      this.postId = entity.toPostId();
      this.imageUrl = entity.getImageUrl();
      this.priority = entity.getPriority();
    }

  }

}
