package com.setge.dddpost.domain.post.application;

import com.setge.dddpost.domain.post.application.PostDto.ChangeRecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.PostRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;


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
    dto.Update(existingPost);
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

}
