package com.setge.dddpost.domain.nestedcomment.domain;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentSearchDto;
import com.setge.dddpost.domain.nestedcomment.infrastructure.NestedCommentQueryRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NestedCommentDomainService {

  private final NestedCommentRepository nestedCommentRepository;
  private final NestedCommentQueryRepository nestedCommentQueryRepository;


  public NestedComment getNestedComment(final Long id) {
    return nestedCommentRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "nestedComment.notFound", id)
    );
  }

  public NestedCommentSearchDto getNestedCommentSearchDto(final Long id) {
    return nestedCommentQueryRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "nestedComment.notFound", id)
    );
  }
}
