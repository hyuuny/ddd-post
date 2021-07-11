package com.setge.dddpost.domain.comment.application;

import com.setge.dddpost.domain.comment.application.CommentDto.Create;
import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentDto.Update;
import com.setge.dddpost.domain.comment.domain.Comment;
import com.setge.dddpost.domain.comment.domain.CommentDomainService;
import com.setge.dddpost.domain.comment.domain.CommentPostMapper;
import com.setge.dddpost.domain.comment.domain.CommentRepository;
import com.setge.dddpost.domain.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final CommentPostMapper commentPostMapper;
  private final CommentDomainService domainService;


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
    CommentSearchDto comment = domainService.findSearchDtoById(id);
    comment.addNestedComments(domainService.findSearchNestedCommentById(id));
    return new Response(comment);
  }

  @Transactional
  @Override
  public Response updateComment(final Long id, Update dto) {
    Comment existingComment = domainService.findById(id);
    dto.update(existingComment);
    return getComment(id);
  }

  @Transactional
  @Override
  public void deleteComment(final Long id) {
    commentRepository.deleteById(id);
  }

  @Override
  public Page<Response> retrieveComment(DetailedSearchCondition searchCondition,
      Pageable pageable) {
    Page<CommentSearchDto> search = domainService.search(searchCondition, pageable);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  private List<Response> toResponses(Page<CommentSearchDto> search) {
    return search.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

}
