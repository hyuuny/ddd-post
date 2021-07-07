package com.setge.dddpost.domain.nestedcomment.application;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Create;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Response;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Update;

public interface NestedCommentService {

  Response createNestedComment(final Long commentId, Create dto);

  Response getNestedComment(final Long id);

  Response updateNestedComment(final Long id, Update dto);

  void deleteNestedComment(final Long id);


}
