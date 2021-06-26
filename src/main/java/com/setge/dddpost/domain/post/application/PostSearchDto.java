package com.setge.dddpost.domain.post.application;

import com.setge.dddpost.domain.post.application.PostImageDto.Response;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.PostImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchDto {

  private Post post;
  private Long userId;
  private String nickname;

  public Long getId() {
    return post.getId();
  }

  public String getTitle() {
    return post.getTitle();
  }

  public String getContent() {
    return post.getContent();
  }

  public boolean isRecommend() {
    return post.isRecommend();
  }

  public List<Response> getPostImages() {
    return post.getPostImages().stream()
        .map(PostImageDto.Response::new)
        .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
        .collect(Collectors.toList());
  }

  public Long getUserId() {
    return userId;
  }

  public String getNickname() {
    return nickname;
  }

  public LocalDateTime getCreatedAt() {
    return post.getCreatedAt();
  }

  public LocalDateTime getLastModifiedAt() {
    return post.getLastModifiedAt();
  }

  public String toType() {
    return post.getType().toString();
  }

}
