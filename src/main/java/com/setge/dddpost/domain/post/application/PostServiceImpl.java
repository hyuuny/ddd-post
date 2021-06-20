package com.setge.dddpost.domain.post.application;

import com.setge.dddpost.domain.post.application.PostDto.ChangeRecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.SearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.PostRepository;
import com.setge.dddpost.domain.post.infrastructure.PostQueryRepository;
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
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final PostQueryRepository postQueryRepository;

  @Transactional
  @Override
  public Response createPost(Create dto) {
    Post post = dto.toEntity();
    Long postId = postRepository.save(post).getId();
    return getPost(postId);
  }

  @Override
  public Response getPost(final Long id) {
    Post post = findPostById(id);
    return toResponse(post);
  }

  private Response toResponse(Post post) {
    return new Response(post);
  }

  private Post findPostById(Long id) {
    return postRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "post.notFound", id)
    );
  }

  @Transactional
  @Override
  public Response updatePost(final Long id, Update dto) {
    Post existingPost = findPostById(id);
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
      Post existingPost = postRepository.findById(recommendPost.getId()).orElseThrow(
          () -> new HttpStatusMessageException(
              HttpStatus.BAD_REQUEST, "post.notFound", recommendPost.getId()));
      existingPost.changeRecommendPost(recommendPost.getRecommend());
    });
  }

  @Override
  public Page<Response> retrievePost(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<Post> search = postQueryRepository.search(searchCondition, pageable);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  @Override
  public Page<Response> retrievePost(SearchCondition searchCondition, Pageable pageable) {
    Page<Post> search = postQueryRepository.search(searchCondition, pageable);
    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  public List<Response> toResponses(Page<Post> searchDtos) {
    return searchDtos.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

}
