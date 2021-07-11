package com.setge.dddpost.domain.post.application;

import com.setge.dddpost.domain.comment.application.CommentDto;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.post.application.PostImageDto.Response;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchDto {

  private Long id;
  private PostType type;
  private String title;
  private String content;
  private boolean recommend;
  private List<PostImageDto.Response> postImages;
  private List<CommentDto.Response> comments;
  private Long userId;
  private String nickname;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;

  public String toType() {
    return type.toString();
  }

  public void addPossImages(List<Response> postImages) {
    this.postImages = postImages;
  }

}
