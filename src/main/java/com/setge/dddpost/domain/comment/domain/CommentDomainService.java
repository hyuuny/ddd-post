package com.setge.dddpost.domain.comment.domain;

import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentSearchDto;
import com.setge.dddpost.domain.comment.infrastructure.CommentQueryRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Response;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDomainService {

  private final CommentRepository commentRepository;
  private final CommentQueryRepository commentQueryRepository;


  public Comment findById(Long id) {
    return commentRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "comment.notFound", id));
  }

  public CommentSearchDto findSearchDtoById(Long id) {
    return commentQueryRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "comment.notFound", id));
  }

  public List<Response> findSearchNestedCommentById(Long id) {
    return commentQueryRepository.findNestedCommentsById(id).stream()
        .map(NestedCommentDto.Response::new)
        .collect(Collectors.toList());
  }

  public Page<CommentSearchDto> search(DetailedSearchCondition searchCondition, Pageable pageable) {
    return commentQueryRepository.search(searchCondition, pageable);
  }

}
