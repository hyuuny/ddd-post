package com.setge.dddpost.domain.comment.infrastructure;

import static com.querydsl.core.types.Projections.fields;
import static com.setge.dddpost.domain.comment.domain.QComment.comment;
import static com.setge.dddpost.domain.member.domain.QMember.member;
import static com.setge.dddpost.domain.nestedcomment.domain.QNestedComment.nestedComment;
import static com.setge.dddpost.domain.post.domain.QPost.post;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentSearchDto;
import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentSearchDto;
import com.setge.dddpost.global.jpa.Querydsl4RepositorySupport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CommentQueryRepository extends Querydsl4RepositorySupport {

  protected CommentQueryRepository() {
    super(Comment.class);
  }


  public Optional<CommentSearchDto> findById(final Long id) {
    CommentSearchDto searchDto = getQueryFactory()
        .select(fields(CommentSearchDto.class,
            comment.id.as("id"),
            comment.post.id.as("postId"),
            comment.userId.as("userId"),
            member.nickname.as("nickname"),
            comment.content.as("content"),
            comment.createdAt.as("createdAt"),
            comment.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(comment)
        .join(member).on(comment.userId.eq(member.id))
        .where(
            comment.id.eq(id)
        )
        .fetchOne();

    return Optional.ofNullable(searchDto);
  }

  public List<CommentSearchDto> findSearchDtosById(final Long id) {
    return getQueryFactory()
        .select(fields(CommentSearchDto.class,
            comment.id.as("id"),
            comment.post.id.as("postId"),
            comment.userId.as("userId"),
            member.nickname.as("nickname"),
            comment.content.as("content"),
            comment.createdAt.as("createdAt"),
            comment.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(comment)
        .join(member).on(comment.userId.eq(member.id))
        .where(
            comment.post.id.eq(id)
        )
        .fetch();
  }

  public List<NestedCommentSearchDto> findNestedCommentsById(final Long id) {
    return getQueryFactory()
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
            nestedComment.comment.id.eq(id)
        )
        .fetch();
  }

  public List<NestedCommentSearchDto> findNestedCommentsByIds(List<Long> commentIds) {
    return getQueryFactory()
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
            nestedComment.comment.id.in(commentIds)
        )
        .fetch();
  }

  public Page<CommentSearchDto> search(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(CommentSearchDto.class,
            comment.id.as("id"),
            comment.post.id.as("postId"),
            comment.userId.as("userId"),
            member.nickname.as("nickname"),
            comment.content.as("content"),
            comment.createdAt.as("createdAt"),
            comment.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(comment)
        .join(member).on(comment.userId.eq(member.id))
        .where(
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword())
        )
    );
  }

  private BooleanExpression keywordSearch(final String searchOption, final String keyword) {
    if (isEmpty(searchOption) || isEmpty(keyword)) {
      return null;
    }

    if ("nickname".equals(searchOption)) {
      return member.nickname.like("%" + keyword + "%");
    }

    return null;
  }


}
