package com.setge.dddpost.domain.member.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.member.domain.Member.MemberStatus;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class MemberDto {

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "회원 등록")
  public static class Join {

    @NotEmpty
    @ApiModelProperty(value = "이메일", example = "exam@naver.com", required = true, position = 1)
    private String email;

    @Min(8)
    @NotEmpty
    @ApiModelProperty(value = "비밀번호", example = "secret", required = true, position = 2)
    private String password;

    @NotEmpty
    @ApiModelProperty(value = "닉네임", example = "두덕", required = true, position = 3)
    private String nickname;

    public Member toEntity() {
      return Member.builder()
          .email(this.email)
          .password(this.password)
          .nickname(this.nickname)
          .status(MemberStatus.ACTIVATION)
          .build();
    }

  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @ApiModel(value = "이메일 중복 확인")
  public static class CheckEmail {

    @NotEmpty
    @ApiModelProperty(value = "이메일", example = "exam@naver.com", required = true, position = 1)
    private String email;

  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @ApiModel(value = "이메일 닉네임 중복 확인")
  public static class CheckNickname {

    @NotEmpty
    @ApiModelProperty(value = "닉네임", example = "두덕", required = true, position = 1)
    private String nickname;

  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @ApiModel(value = "회원정보 수정")
  public static class Update {

    @Min(8)
    @NotEmpty
    @ApiModelProperty(value = "비밀번호", example = "secret", required = true, position = 1)
    private String password;

    @NotEmpty
    @ApiModelProperty(value = "닉네임", example = "두덕", required = true, position = 2)
    private String nickname;

    public void update(Member entity) {
      entity.changePassword(this.password);
      entity.changeNickname(this.nickname);
    }

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "posts")
  @JsonInclude(Include.NON_EMPTY)
  @ApiModel(value = "회원")
  public static class Response {

    @ApiModelProperty(value = "회원 ID", example = "1", required = true, position = 1)
    private Long id;

    @ApiModelProperty(value = "회원 이메일", example = "exam@naver.com", required = true, position = 2)
    private String email;

    @ApiModelProperty(value = "회원 닉네임", example = "두덕", required = true, position = 3)
    private String nickname;

    @ApiModelProperty(value = "회원 닉네임", example = "두덕", required = true, position = 4)
    private String status;

    @ApiModelProperty(value = "등록일시", example = "2020-06-13T21:18:58.139065", required = true, position = 5)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "최종수정일", example = "2020-06-13T21:18:58.139065", required = true, position = 6)
    private LocalDateTime lastModifiedAt;

    public Response(Member entity) {
      this.id = entity.getId();
      this.email = entity.getEmail();
      this.nickname = entity.getNickname();
      this.status = entity.toStatus();
      this.createdAt = entity.getCreatedAt();
      this.lastModifiedAt = entity.getLastModifiedAt();
    }

    public Response(MemberSearchDto entity) {
      this.id = entity.getId();
      this.email = entity.getEmail();
      this.nickname = entity.getNickname();
      this.status = entity.toStatus();
      this.createdAt = entity.getCreatedAt();
      this.lastModifiedAt = entity.getLastModifiedAt();
    }

  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @ApiModel(value = "회원 상세검색조건")
  public static class DetailedSearchCondition {

    @ApiModelProperty(value = "검색 옵션", example = "email", required = false, position = 1)
    private String searchOption;

    @ApiModelProperty(value = "검색 키워드", example = "exam@naver.com", required = false, position = 2)
    private String keyword;

    @ApiModelProperty(value = "회원 상태", example = "exam@naver.com", required = false, position = 3)
    private MemberStatus status;

  }

}
