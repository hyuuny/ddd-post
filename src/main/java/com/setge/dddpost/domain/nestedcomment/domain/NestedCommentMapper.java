package com.setge.dddpost.domain.nestedcomment.domain;

import com.setge.dddpost.domain.comment.domain.Comment;

public interface NestedCommentMapper {

  Comment getComment(final Long id);

}
