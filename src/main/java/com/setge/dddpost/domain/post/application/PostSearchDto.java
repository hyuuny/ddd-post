package com.setge.dddpost.domain.post.application;

import com.setge.dddpost.domain.post.domain.Post.PostType;
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

  private Long id;
  private String type;
  private String title;
  private String content;
  private String nickname;
  private boolean recommend;
  private List<PostImage> postImages;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;

  public List<PostImageDto.Response> getPostImages() {
    return postImages.stream()
        .map(PostImageDto.Response::new)
        .collect(Collectors.toList());
  }

}
