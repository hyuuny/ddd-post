package com.setge.dddpost.domain.post.infrastructure;

import static com.setge.dddpost.domain.post.domain.QPost.post;
import static com.setge.dddpost.domain.post.domain.QPostImage.postImage;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.Post.PostType;
import com.setge.dddpost.global.jpa.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryRepository extends Querydsl4RepositorySupport {

  protected PostQueryRepository() {
    super(Post.class);
  }

  public Page<Post> search(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .selectFrom(post)
        .leftJoin(postImage).on(post.id.eq(postImage.post.id)).fetchJoin().distinct()
        .where(
            postRecommendEq(searchCondition.getRecommend()),
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword()),
            postTypeEq(searchCondition.getType())
        )
    );
  }

  private BooleanExpression postTypeEq(PostType type) {
    return isEmpty(type) ? null : post.type.eq(type);
  }

  private BooleanExpression postRecommendEq(final Boolean recommend) {
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
