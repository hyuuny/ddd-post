package com.setge.dddpost.domain.nestedcomment.infrastructure;

import static com.querydsl.core.types.Projections.fields;
import static com.setge.dddpost.domain.member.domain.QMember.member;
import static com.setge.dddpost.domain.nestedcomment.domain.QNestedComment.nestedComment;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentSearchDto;
import com.setge.dddpost.domain.nestedcomment.domain.NestedComment;
import com.setge.dddpost.global.jpa.Querydsl4RepositorySupport;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class NestedCommentQueryRepository extends Querydsl4RepositorySupport {

  protected NestedCommentQueryRepository() {
    super(NestedComment.class);
  }

  public Optional<NestedCommentSearchDto> findById(final Long id) {
    NestedCommentSearchDto searchDto = getQueryFactory()
        .select(fields(NestedCommentSearchDto.class,
            nestedComment.id.as("id"),
            nestedComment.comment.id.as("commentId"),
            nestedComment.userId.as("userId"),
            member.nickname.as("nickname"),
            nestedComment.content.as("content"),
            nestedComment.createdAt.as("createdAt"),
            nestedComment.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(nestedComment)
        .join(member).on(nestedComment.userId.eq(member.id))
        .where(
            nestedComment.id.eq(id)
        )
        .fetchOne();

    return Optional.ofNullable(searchDto);
  }

}
