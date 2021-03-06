package com.setge.dddpost.domain.post.domain;

import com.setge.dddpost.domain.comment.application.CommentSearchDto;
import com.setge.dddpost.domain.comment.infrastructure.CommentQueryRepository;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentSearchDto;
import com.setge.dddpost.domain.nestedcomment.infrastructure.NestedCommentQueryRepository;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.SearchCondition;
import com.setge.dddpost.domain.post.application.PostImageDto;
import com.setge.dddpost.domain.post.application.PostImageDto.Response;
import com.setge.dddpost.domain.post.application.PostSearchDto;
import com.setge.dddpost.domain.post.infrastructure.PostQueryRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostDomainService {

  private final PostRepository postRepository;
  private final PostQueryRepository postQueryRepository;
  private final CommentQueryRepository commentQueryRepository;
  private final NestedCommentQueryRepository nestedCommentQueryRepository;


  public Post getPost(final Long id) {
    return postRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "post.notFound", id)
    );
  }

  public PostSearchDto getPostSearchDto(final Long id) {
    return postQueryRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "post.notFound", id)
    );
  }

  public List<CommentSearchDto> getComments(final Long id) {
    return commentQueryRepository.findSearchDtosById(id);
  }

  public List<NestedCommentSearchDto> getNestedComments(List<Long> commentIds) {
    return nestedCommentQueryRepository.findSearchDtosById(commentIds);
  }

  public List<Response> getPostImages(final Long id) {
    return postQueryRepository.findPostImagesById(id).stream()
        .map(PostImageDto.Response::new)
        .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
        .collect(Collectors.toList());
  }

  public Page<PostSearchDto> search(DetailedSearchCondition searchCondition, Pageable pageable) {
    return postQueryRepository.search(searchCondition, pageable);
  }

  public Page<PostSearchDto> search(SearchCondition searchCondition, Pageable pageable) {
    return postQueryRepository.search(searchCondition, pageable);
  }

  public Map<Long, List<Response>> getPostIdMap(List<Long> postIds) {
    return postQueryRepository
        .findPostImagesByIds(postIds).stream()
        .map(PostImageDto.Response::new)
        .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
        .collect(Collectors.groupingBy(PostImageDto.Response::getPostId));
  }

}
