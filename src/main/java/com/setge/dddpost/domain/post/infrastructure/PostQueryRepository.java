package com.setge.dddpost.domain.post.infrastructure;

import static com.querydsl.core.types.Projections.fields;
import static com.setge.dddpost.domain.post.domain.QPost.post;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostSearchDto;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.global.jpa.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PostQueryRepository extends Querydsl4RepositorySupport {

  protected PostQueryRepository() {
    super(Post.class);
  }

  public Page<PostSearchDto> search(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(PostSearchDto.class,
            post.id,
            post.type,
            post.title,
            post.content,
            post.nickname,
            post.recommend,
            post.createdAt,
            post.lastModifiedAt
        ))
        .from(post)
        .where(
            PostRecommendEq(searchCondition.getRecommend()),
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword())
        )
    );
  }

  private BooleanExpression PostRecommendEq(final Boolean recommend) {
    return isEmpty(recommend) ? null : recommend ? post.recommend.isTrue() : post.recommend.isFalse();
  }

  private BooleanExpression keywordSearch(final String searchOption, final String keyword) {
    if (isEmpty(searchOption) || isEmpty(keyword)) {
      return null;
    }

    if ("title".equals(searchOption)) {
      return post.title.like("%" + keyword + "%");
    }

    if ("nickname".equals(searchOption)) {
      return post.nickname.like("%" + keyword + "%");
    }

      return null;
    }


}
