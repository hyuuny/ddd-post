package com.setge.dddpost.domain.comment.application;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSearchDto {

  private Long id;
  private Long postId;
  private Long userId;
  private String nickname;
  private String content;
  private List<NestedCommentDto.Response> nestedComments;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;

  public void addNestedComments(List<NestedCommentDto.Response> nestedComments) {
    this.nestedComments = nestedComments;
  }

}
