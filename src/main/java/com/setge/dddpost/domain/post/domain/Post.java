package com.setge.dddpost.domain.post.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import com.google.common.collect.Lists;
import com.setge.dddpost.global.common.BaseEntity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
public class Post extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Lob
  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String nickname;

  private boolean recommend;

  @OneToMany(mappedBy = "post", cascade = ALL, fetch = LAZY, orphanRemoval = true)
  private List<PostImage> postImages = Lists.newArrayList();

  public void changeTitle(String title) {
    this.title = title;
  }

  public void changeContent(String content) {
    this.content = content;
  }

  public void changeRecommendPost(boolean recommend) {
    this.recommend = recommend;
  }

  public void addImage(PostImage image) {
    image.setPostImage(this);
  }



}
