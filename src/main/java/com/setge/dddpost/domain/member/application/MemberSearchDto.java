package com.setge.dddpost.domain.member.application;

import com.setge.dddpost.domain.member.domain.Member.MemberStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MemberSearchDto {

  private Long id;
  private String email;
  private String nickname;
  private MemberStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;

  public String toStatus() {
    return status.toString();
  }


}
