package com.setge.dddpost.domain.nestedcomment.domain;

import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.global.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NestedComment extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Comment comment;

  @Column(nullable = false)
  private Long userId;

  private String content;


  public void changeContent(String content) {
    this.content = content;
  }

  public Long toCommentId() {
    return comment.getId();
  }

}
