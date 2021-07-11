package com.setge.dddpost.domain.post.infrastructure;

import static com.querydsl.core.types.Projections.fields;
import static com.setge.dddpost.domain.member.domain.QMember.member;
import static com.setge.dddpost.domain.post.domain.QPost.post;
import static com.setge.dddpost.domain.post.domain.QPostImage.postImage;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.SearchCondition;
import com.setge.dddpost.domain.post.application.PostSearchDto;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import com.setge.dddpost.domain.post.domain.PostImage;
import com.setge.dddpost.global.jpa.Querydsl4RepositorySupport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryRepository extends Querydsl4RepositorySupport {

  protected PostQueryRepository() {
    super(Post.class);
  }


  public Optional<PostSearchDto> findById(final Long id) {
    PostSearchDto searchDto = getQueryFactory()
        .select(fields(PostSearchDto.class,
            post.id.as("id"),
            post.type.as("type"),
            post.title.as("title"),
            post.content.as("content"),
            post.recommend.as("recommend"),
            member.id.as("userId"),
            member.nickname.as("nickname"),
            post.createdAt.as("createdAt"),
            post.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(post)
        .join(member).on(post.userId.eq(member.id))
        .where(
            post.id.eq(id)
        ).fetchOne();

    return Optional.ofNullable(searchDto);
  }

  public List<PostImage> findPostImagesById(final Long id) {
    return getQueryFactory()
        .selectFrom(postImage)
        .where(
            postImage.post.id.eq(id)
        ).fetch();
  }

  public Page<PostSearchDto> search(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(PostSearchDto.class,
            post.id.as("id"),
            post.type.as("type"),
            post.title.as("title"),
            post.recommend.as("recommend"),
            member.nickname.as("nickname"),
            post.createdAt.as("createdAt"),
            post.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(post)
        .join(member).on(post.userId.eq(member.id))
        .where(
            postRecommendEq(searchCondition.getRecommend()),
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword()),
            postTypeEq(searchCondition.getType())
        )
    );
  }

  public List<PostImage> findPostImagesByIds(List<Long> postIds) {
    return getQueryFactory()
        .selectFrom(postImage)
        .where(
            postImage.post.id.in(postIds)
        ).fetch();
  }

  public Page<PostSearchDto> search(
      SearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(PostSearchDto.class,
            post.id.as("id"),
            post.type.as("type"),
            post.title.as("title"),
            post.recommend.as("recommend"),
            member.nickname.as("nickname"),
            post.createdAt.as("createdAt"),
            post.lastModifiedAt.as("lastModifiedAt")
        ))
        .from(post)
        .join(member).on(post.userId.eq(member.id))
        .where(
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword())
        )
    );
  }

  private BooleanExpression postTypeEq(PostType type) {
    return isEmpty(type) ? null : post.type.eq(type);
  }

  private BooleanExpression postRecommendEq(final Boolean recommend) {
    return isEmpty(recommend) ? null
        : recommend ? post.recommend.isTrue() : post.recommend.isFalse();
  }

  private BooleanExpression keywordSearch(final String searchOption, final String keyword) {
    if (isEmpty(searchOption) || isEmpty(keyword)) {
      return null;
    }

    if ("title".equals(searchOption)) {
      return post.title.like("%" + keyword + "%");
    }

    if ("nickname".equals(searchOption)) {
      return member.nickname.like("%" + keyword + "%");
    }

    return null;
  }


}
