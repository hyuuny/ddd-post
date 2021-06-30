package com.setge.dddpost.domain.comment.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

public class CommentDto {

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "댓글 등록")
  public static class Create {

    @NotNull
    @ApiModelProperty(value = "유저 ID", example = "1", required = true, position = 1)
    private Long userId;

    @NotEmpty
    @ApiModelProperty(value = "댓글 내용", example = "너무 재밌네요 ㅋㅋ", required = true, position = 2)
    private String content;

    public Comment toEntity() {
      return Comment.builder()
          .userId(this.userId)
          .content(this.content)
          .build();
    }
  }

  @Setter
  @Getter
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  @Relation(collectionRelation = "comments")
  @JsonInclude(Include.NON_EMPTY)
  @ApiModel(value = "댓글 조회")
  public static class Response {

    @ApiModelProperty(value = "ID", example = "1", required = true, position = 1)
    private Long id;

    @ApiModelProperty(value = "게시물 ID", example = "1", required = true, position = 2)
    private Long postId;

    @ApiModelProperty(value = "유저 ID", example = "1", required = true, position = 3)
    private Long userId;

    @ApiModelProperty(value = "닉네임", example = "두덕", required = true, position = 4)
    private String nickname;

    @ApiModelProperty(value = "댓글 내용", example = "너무 재밌네요 ㅋㅋ", required = true, position = 5)
    private String content;

    @ApiModelProperty(value = "대댓글", required = false, position = 6)
    private List<NestedCommentDto.Response> nestedComments = Lists.newArrayList();

    @ApiModelProperty(value = "등록일", required = false, position = 7)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일", required = false, position = 8)
    private LocalDateTime lastModifiedAt;

    public Response(Comment entity) {
      this.id = entity.getId();
      this.postId = entity.toPostId();
      this.userId = entity.getUserId();
      this.content = entity.getContent();
      this.createdAt = entity.getCreatedAt();
    }

    public Response(CommentSearchDto entity) {
      this.id = entity.getId();
      this.postId = entity.getPostId();
      this.userId = entity.getUserId();
      this.nickname = entity.getNickname();
      this.content = entity.getContent();
      this.nestedComments = entity.getNestedComments();
      this.createdAt = entity.getCreatedAt();
      this.lastModifiedAt = entity.getLastModifiedAt();
    }

  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "댓글 수정")
  public static class Update {

    @NotEmpty
    @ApiModelProperty(value = "댓글 내용", example = "너무 재밌네요 ㅋㅋ", required = true, position = 1)
    private String content;

    public void update(Comment entity) {
      entity.changeContent(this.content);
    }
  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "댓글 검색조건")
  public static class DetailedSearchCondition {

    @ApiModelProperty(value = "검색 옵션", example = "nickname", required = false, position = 1)
    private String searchOption;

    @ApiModelProperty(value = "검색 키워드", example = "두덕", required = false, position = 2)
    private String keyword;

  }

}
