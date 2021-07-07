package com.setge.dddpost.domain.nestedcomment.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.setge.dddpost.domain.nestedcomment.domain.NestedComment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
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

public class NestedCommentDto {

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "대댓글 등록")
  public static class Create {

    @NotNull
    @ApiModelProperty(value = "유저 ID", example = "1", required = true, position = 1)
    private Long userId;

    @NotEmpty
    @ApiModelProperty(value = "댓글 내용", example = "댓글쓰신 분 너무 웃겨요 ㅋㅋ", required = true, position = 2)
    private String content;

    public NestedComment toEntity() {
      return NestedComment.builder()
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
  @Relation(collectionRelation = "orders")
  @JsonInclude(Include.NON_EMPTY)
  @ApiModel(value = "대댓글 조회")
  public static class Response {

    @ApiModelProperty(value = "ID", example = "1", required = true, position = 1)
    private Long id;

    @ApiModelProperty(value = "댓글 ID", example = "1", required = true, position = 2)
    private Long commentId;

    @ApiModelProperty(value = "유저 ID", example = "1", required = true, position = 3)
    private Long userId;

    @ApiModelProperty(value = "닉네임", example = "브브", required = true, position = 4)
    private String nickname;

    @ApiModelProperty(value = "댓글 내용", example = "댓글쓰신 분 너무 웃겨요 ㅋㅋ", required = true, position = 5)
    private String content;

    @ApiModelProperty(value = "등록일", required = false, position = 6)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "등록일", required = false, position = 7)
    private LocalDateTime lastModifiedAt;

    public Response(NestedCommentSearchDto entity) {
      this.id = entity.getId();
      this.commentId = entity.getCommentId();
      this.userId = entity.getUserId();
      this.nickname = entity.getNickname();
      this.content = entity.getContent();
      this.createdAt = entity.getCreatedAt();
      this.lastModifiedAt = entity.getLastModifiedAt();
    }

  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "대댓글 수정")
  public static class Update {

    @ApiModelProperty(value = "대댓글 내용", example = "댓글쓰신 분 너무 웃겨요 ㅋㅋ", required = true, position = 1)
    private String content;

    public void update(NestedComment entity) {
      entity.changeContent(this.getContent());
    }
  }

}

