package com.setge.dddpost.domain.member.infrastructure;

import static com.querydsl.core.types.Projections.fields;
import static com.setge.dddpost.domain.member.domain.QMember.member;
import static com.setge.dddpost.domain.post.domain.QPost.post;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberSearchDto;
import com.setge.dddpost.domain.member.domain.Member;
import com.setge.dddpost.domain.member.domain.Member.MemberStatus;
import com.setge.dddpost.global.jpa.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MemberQueryRepository extends Querydsl4RepositorySupport {

  protected MemberQueryRepository() {
    super(Member.class);
  }

  public Page<MemberSearchDto> findAllMembers(Pageable pageable) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(MemberSearchDto.class,
            member.id,
            member.email,
            member.nickname,
            member.status,
            member.createdAt,
            member.lastModifiedAt
        ))
        .from(member)
    );
  }

  public Page<MemberSearchDto> search(DetailedSearchCondition searchCondition,
      Pageable pageable) {

    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(MemberSearchDto.class,
            member.id,
            member.email,
            member.nickname,
            member.status,
            member.createdAt,
            member.lastModifiedAt
        ))
        .from(member)
        .where(
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword()),
            memberStatusEq(searchCondition.getStatus())
        )
    );
  }


  private BooleanExpression memberStatusEq(final MemberStatus status) {
    return isEmpty(status) ? null : member.status.eq(status);

  }

  private BooleanExpression keywordSearch(final String searchOption, final String keyword) {
    if (isEmpty(searchOption) || isEmpty(keyword)) {
      return null;
    }

    if ("email".equals(searchOption)) {
      return member.email.like("%" + keyword + "%");
    }

    if ("nickname".equals(searchOption)) {
      return member.nickname.like("%" + keyword + "%");
    }

    return null;
  }

}
