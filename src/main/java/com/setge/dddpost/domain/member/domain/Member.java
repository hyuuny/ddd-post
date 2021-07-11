package com.setge.dddpost.domain.member.domain;

import com.setge.dddpost.global.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseEntity {

  @Getter
  @AllArgsConstructor
  public enum MemberStatus{

    ACTIVATION("활성화"),
    DISABLE("비활성화");

    public final String title;
  }

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickname;

  private MemberStatus status;

  public void changePassword(String password) {
    this.password = password;
  }

  public void changeNickname(String nickname) {
    this.nickname = nickname;
  }

  public void leaveMember() {
    this.status = MemberStatus.DISABLE;
  }

  public String toStatus() {
    return status.toString();
  }

  public void create(MemberValidator validator) {
    validator.validate(this);
  }

}
