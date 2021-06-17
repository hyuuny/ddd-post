package com.setge.dddpost.domain.post.application;

import static com.setge.dddpost.domain.post.application.PostDto.Response;

import com.setge.dddpost.domain.post.application.PostDto.ChangeRecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

  Response createPost(Create dto);

  Response getPost(final Long id);

  Response updatePost(final Long id, Update dto);

  void deletePost(final Long id);

  void changeRecommendPost(ChangeRecommendPost dto);

  Page<Response> retrievePost(DetailedSearchCondition searchCondition, Pageable pageable);

}
