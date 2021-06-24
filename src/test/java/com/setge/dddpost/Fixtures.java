package com.setge.dddpost;

import com.setge.dddpost.domain.member.application.MemberDto;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.post.application.PostDto;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.domain.Post.PostType;

public class Fixtures {


  public static PostDto.Create.CreateBuilder anPost() {
    return Create.builder()
        .type(PostType.FUNNY)
        .title("재미있는 자료 올립니다 ㅋㅋㅋ")
        .content("6월 13일 있었던 일 ㅋㅋㅋ");
  }

  public static MemberDto.Join.JoinBuilder anJoin() {
    return Join.builder()
        .email("exam@naver.com")
        .password("12341234")
        .nickname("두덕");
  }



}
