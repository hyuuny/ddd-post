package com.setge.dddpost.domain.comment.domain;

import com.setge.dddpost.domain.post.domain.Post;

public interface CommentPostMapper {

  Post findPostById(final Long postId);

}
