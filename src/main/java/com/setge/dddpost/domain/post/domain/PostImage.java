package com.setge.dddpost.domain.post.domain;

import static javax.persistence.FetchType.LAZY;

import com.setge.dddpost.global.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class PostImage extends BaseEntity {

  @ManyToOne(fetch = LAZY, optional = false)
  private Post post;

  @Column(nullable = false)
  private String imageUrl;

  private int priority;

  public void setPostImage(Post post) {
    if (this.post != null) {
      post.getPostImages().remove(this);
    }
    this.post = post;
    this.post.getPostImages().add(this);
  }

  public Long toPostId() {
    return post.getId();
  }

}
