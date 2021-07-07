package com.setge.dddpost.domain.nestedcomment.application;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NestedCommentSearchDto {

  private Long id;
  private Long commentId;
  private Long userId;
  private String nickname;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;


}
