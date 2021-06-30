package com.setge.dddpost.domain.comment.application;

import com.setge.dddpost.domain.comment.application.CommentDto.Create;
import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentDto.Update;
import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.domain.comment.domain.CommentPostMapper;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.comment.infrastructure.CommentQueryRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final CommentPostMapper commentPostMapper;
  private final CommentQueryRepository commentQueryRepository;


  @Transactional
  @Override
  public Response createComment(final Long postId, Create dto) {
    Comment comment = dto.toEntity();
    Post post = commentPostMapper.findPostById(postId);
    comment.setPost(post);
    return getComment(commentRepository.save(comment).getId());
  }

  @Override
  public Response getComment(final Long id) {
    CommentSearchDto comment = searchById(id);
    comment.addNestedComments(searchNestedComment(id));
    return new Response(comment);
  }

  private List<NestedCommentDto.Response> searchNestedComment(Long id) {
    return commentQueryRepository.findNestedCommentsById(id).stream()
        .map(NestedCommentDto.Response::new)
        .collect(Collectors.toList());
  }

  private CommentSearchDto searchById(Long id) {
    return commentQueryRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "comment.notFound", id));
  }

  @Transactional
  @Override
  public Response updateComment(final Long id, Update dto) {
    Comment existingComment = findById(id);
    dto.update(existingComment);
    return getComment(id);
  }

  private Comment findById(Long id) {
    return commentRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "comment.notFound", id));
  }

  @Transactional
  @Override
  public void deleteComment(final Long id) {
    commentRepository.deleteById(id);
  }

  @Override
  public Page<Response> retrieveComment(DetailedSearchCondition searchCondition,
      Pageable pageable) {
    Page<CommentSearchDto> search = commentQueryRepository.search(searchCondition, pageable);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  private List<Response> toResponses(Page<CommentSearchDto> search) {
    return search.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

}
