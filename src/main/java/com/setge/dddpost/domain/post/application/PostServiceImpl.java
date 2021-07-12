package com.setge.dddpost.domain.post.application;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.setge.dddpost.domain.comment.application.CommentDto;
import com.setge.dddpost.domain.comment.application.CommentSearchDto;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto;
import com.setge.dddpost.domain.post.application.PostDto.ChangeRecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.SearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.PostDomainService;
import com.setge.dddpost.domain.post.domain.PostRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import java.util.List;
import java.util.Map;
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
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final PostDomainService domainService;

  @Transactional
  @Override
  public Response createPost(Create dto) {
    Post post = dto.toEntity();
    return getPost(postRepository.save(post).getId());
  }

  @Override
  public Response getPost(final Long id) {
    PostSearchDto post = domainService.getPostSearchDto(id);
    List<CommentSearchDto> comments = domainService.getComments(id);
    addNestedCommentToComment(comments, toNestedCommentMap(toCommentIds(comments)));

    post.addComments(toComments(comments));
    post.addPossImages(domainService.getPostImages(id));
    return toResponse(post);
  }

  @Transactional
  @Override
  public Response updatePost(final Long id, Update dto) {
    Post existingPost = domainService.getPost(id);
    dto.update(existingPost);
    return getPost(id);
  }

  @Transactional
  @Override
  public void deletePost(final Long id) {
    postRepository.deleteById(id);
  }

  @Transactional
  @Override
  public void changeRecommendPost(ChangeRecommendPost dto) {
    dto.getRecommendPosts().forEach(recommendPost -> {
      Post existingPost = domainService.getPost(recommendPost.getId());
      existingPost.changeRecommendPost(recommendPost.getRecommend());
    });
  }

  @Override
  public Page<Response> retrievePost(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<PostSearchDto> search = domainService.search(searchCondition, pageable);
    addPostImagesToPosts(search);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  @Override
  public Page<Response> retrievePost(SearchCondition searchCondition, Pageable pageable) {
    Page<PostSearchDto> search = domainService.search(searchCondition, pageable);
    addPostImagesToPosts(search);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }


  private void addPostImagesToPosts(Page<PostSearchDto> search) {
    Map<Long, List<PostImageDto.Response>> map = domainService.getPostIdMap(getPostIds(search));
    search.getContent().stream()
        .forEach(post -> post.addPossImages(map.get(post.getId())));
  }

  private List<Long> getPostIds(Page<PostSearchDto> search) {
    return search.stream()
        .map(PostSearchDto::getId)
        .collect(Collectors.toList());
  }

  public List<Response> toResponses(Page<PostSearchDto> searchDtos) {
    return searchDtos.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

  private List<CommentDto.Response> toComments(List<CommentSearchDto> comments) {
    return comments.stream().map(CommentDto.Response::new).collect(Collectors.toList());
  }

  private Map<Long, List<NestedCommentDto.Response>> toNestedCommentMap(List<Long> commentIds) {
    return domainService.getNestedComments(commentIds).stream()
        .map(NestedCommentDto.Response::new)
        .collect(Collectors.groupingBy(NestedCommentDto.Response::getCommentId));
  }

  private List<Long> toCommentIds(List<CommentSearchDto> comments) {
    return comments.stream()
        .map(CommentSearchDto::getId)
        .collect(Collectors.toList());
  }

  private void addNestedCommentToComment(
      List<CommentSearchDto> comments,
      Map<Long, List<NestedCommentDto.Response>> nestedCommentMap
  ) {
    comments.stream()
        .forEach(comment -> {
          if (!isEmpty(nestedCommentMap.get(comment.getId()))) {
            comment.addNestedComments(nestedCommentMap.get(comment.getId()));
          }
        });
  }

  private Response toResponse(PostSearchDto post) {
    return new Response(post);
  }


}
