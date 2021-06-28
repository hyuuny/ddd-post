package com.setge.dddpost.domain.comment.domain;

import static javax.persistence.FetchType.LAZY;

import com.google.common.collect.Lists;
import com.setge.dddpost.domain.nestedcomment.domain.NestedComment;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.global.common.BaseEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends BaseEntity {

  @ManyToOne(optional = false, fetch = LAZY)
  private Post post;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String content;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<NestedComment> nestedComments = Lists.newArrayList();

  public void changeContent(String content) {
    this.content = content;
  }

  public Long toPostId() {
    return post.getId();
  }

}
