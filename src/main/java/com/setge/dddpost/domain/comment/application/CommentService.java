package com.setge.dddpost.domain.comment.application;

import com.setge.dddpost.domain.comment.application.CommentDto.Create;
import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentDto.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  Response createComment(final Long postId, Create dto);

  Response getComment(final Long id);

  Response updateComment(final Long id, Update dto);

  void deleteComment(final Long id);

  Page<Response> retrieveComment(DetailedSearchCondition searchCondition, Pageable pageable);

}
