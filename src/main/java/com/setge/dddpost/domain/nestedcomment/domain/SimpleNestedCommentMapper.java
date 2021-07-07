package com.setge.dddpost.domain.nestedcomment.domain;

import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleNestedCommentMapper implements NestedCommentMapper{

  private final CommentRepository commentRepository;

  @Override
  public Comment findCommentById(Long id) {
    return commentRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "cooment.notFound", id)
    );

  }
}
