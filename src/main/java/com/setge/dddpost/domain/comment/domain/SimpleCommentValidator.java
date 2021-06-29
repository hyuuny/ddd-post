package com.setge.dddpost.domain.comment.domain;

import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.PostRepository;
import com.setge.dddpost.global.exceptiron.HttpStatusMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleCommentValidator implements CommentValidator {

  private final PostRepository postRepository;


  @Override
  public void validate(Long postId, Comment comment) {
    addPostValidation(postId, comment);
  }

  private void addPostValidation(Long postId, Comment comment) {
    Post existingPost = findPostById(postId);
    comment.setPost(existingPost);
  }

  private Post findPostById(Long postId) {
    return postRepository.findById(postId).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "post.notFound", postId)
    );
  }
}
