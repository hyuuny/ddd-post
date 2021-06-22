package com.setge.dddpost.domain.member.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail(final String email);

  Optional<Member> findByNickname(final String nickname);

}
