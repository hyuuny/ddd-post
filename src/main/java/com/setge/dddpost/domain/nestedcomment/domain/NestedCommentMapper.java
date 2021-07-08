package com.setge.dddpost.domain.nestedcomment.domain;

import com.setge.dddpost.domain.comment.domain.Comment;

public interface NestedCommentMapper {

  Comment findCommentById(final Long id);

}
