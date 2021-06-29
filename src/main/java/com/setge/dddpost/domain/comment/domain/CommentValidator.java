package com.setge.dddpost.domain.comment.domain;

public interface CommentValidator {

  void validate(final Long postId, Comment comment);

}
