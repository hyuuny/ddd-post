package com.setge.dddpost.domain.comment.domain;

import com.setge.dddpost.domain.post.domain.Post;
import java.util.List;

public interface CommentPostMapper {

  Post findPostById(final Long postId);

}
