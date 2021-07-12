package com.setge.dddpost;

import com.setge.dddpost.domain.comment.application.CommentDto;
import com.setge.dddpost.domain.member.application.MemberDto;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto;
import com.setge.dddpost.domain.post.application.PostDto;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostImageDto;
import com.setge.dddpost.domain.post.domain.Post.PostType;

public class Fixtures {


  public static PostDto.Create.CreateBuilder anPost() {
    return Create.builder()
        .type(PostType.FUNNY)
        .title("재미있는 자료 올립니다 ㅋㅋㅋ")
        .content("6월 13일 있었던 일 ㅋㅋㅋ");
  }

  public static PostImageDto.Create.CreateBuilder anPostImage() {
    return PostImageDto.Create.builder()
        .imageUrl("imageUrl");

  }

  public static MemberDto.Join.JoinBuilder anJoin() {
    return Join.builder()
        .email("exam@naver.com")
        .password("12341234")
        .nickname("두덕");
  }

  public static CommentDto.Create.CreateBuilder anComment(final Long userId) {
    return CommentDto.Create.builder()
        .userId(userId)
        .content("첫코 달아요!");
  }

  public static NestedCommentDto.Create.CreateBuilder anNestedComment(final Long userId) {
    return NestedCommentDto.Create.builder()
        .userId(userId)
        .content("윗 댓 너무 웃겨요 ㅋㅋㅋ");
  }


}
