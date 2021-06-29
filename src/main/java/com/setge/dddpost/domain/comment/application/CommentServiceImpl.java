package com.setge.dddpost.domain.comment.application;

import com.setge.dddpost.domain.comment.application.CommentDto.Create;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentDto.Update;
import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.comment.domain.CommentValidator;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final CommentValidator commentValidator;


  @Transactional
  @Override
  public Response createComment(final Long postId, Create dto) {
    Comment comment = dto.toEntity();
    commentValidator.validate(postId, comment);
    return getComment(commentRepository.save(comment).getId());
  }

  @Override
  public Response getComment(final Long id) {
    Comment comment = findById(id);
    return new Response(comment);
  }

  private Comment findById(Long id) {
    return commentRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "comment.notFound", id)
    );
  }

  @Transactional
  @Override
  public Response updateComment(final Long id, Update dto) {
    Comment existingComment = findById(id);
    dto.update(existingComment);
    return getComment(id);
  }

  @Transactional
  @Override
  public void deleteComment(final Long id) {
    commentRepository.deleteById(id);
  }
}
