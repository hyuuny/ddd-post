package com.setge.dddpost.domain.nestedcomment.application;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Create;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Response;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Update;
import com.setge.dddpost.domain.nestedcomment.domain.NestedComment;
import com.setge.dddpost.domain.nestedcomment.domain.NestedCommentDomainService;
import com.setge.dddpost.domain.nestedcomment.domain.NestedCommentMapper;
import com.setge.dddpost.domain.nestedcomment.domain.NestedCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NestedCommentServiceImpl implements NestedCommentService {

  private final NestedCommentRepository nestedCommentRepository;
  private final NestedCommentMapper nestedCommentMapper;
  private final NestedCommentDomainService domainService;


  @Transactional
  @Override
  public Response createNestedComment(final Long commentId, Create dto) {
    NestedComment nestedComment = dto.toEntity();
    nestedComment.setComment(nestedCommentMapper.getComment(commentId));
    return getNestedComment(nestedCommentRepository.save(nestedComment).getId());
  }

  @Override
  public Response getNestedComment(final Long id) {
    return new Response(domainService.getNestedCommentSearchDto(id));
  }

  @Transactional
  @Override
  public Response updateNestedComment(final Long id, Update dto) {
    NestedComment existingNestedComment = domainService.getNestedComment(id);
    dto.update(existingNestedComment);
    return getNestedComment(id);
  }

  @Transactional
  @Override
  public void deleteNestedComment(final Long id) {
    nestedCommentRepository.deleteById(id);
  }
}
