package com.setge.dddpost.domain.post.application;

import com.setge.dddpost.domain.post.application.PostDto.ChangeRecommendPost;
import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.DetailedSearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.SearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.domain.Post;
import com.setge.dddpost.domain.post.domain.PostDomainService;
import com.setge.dddpost.domain.post.domain.PostRepository;
import java.util.List;
import java.util.Map;
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
    PostSearchDto postSearchDto = domainService.findPostById(id);
    postSearchDto.addPossImages(domainService.findPostImagesById(id));
    return toResponse(postSearchDto);
  }

  private Response toResponse(PostSearchDto post) {
    return new Response(post);
  }

  @Transactional
  @Override
  public Response updatePost(final Long id, Update dto) {
    Post existingPost = domainService.findById(id);
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
     domainService.saveRecommendPost(recommendPost.getId(), recommendPost.getRecommend());
    });
  }

  @Override
  public Page<Response> retrievePost(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<PostSearchDto> search = domainService.search(searchCondition, pageable);
    Map<Long, List<PostImageDto.Response>> postImagesMap = domainService.getPostIdMap(getPostIds(search));

    search.getContent().stream()
        .forEach(post -> post.addPossImages(postImagesMap.get(post.getId())));

    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  private List<Long> getPostIds(Page<PostSearchDto> search) {
    return search.stream()
        .map(PostSearchDto::getId)
        .collect(Collectors.toList());
  }

  @Override
  public Page<Response> retrievePost(SearchCondition searchCondition, Pageable pageable) {
    Page<PostSearchDto> search = domainService.search(searchCondition, pageable);
    Map<Long, List<PostImageDto.Response>> postImagesMap = domainService.getPostIdMap(getPostIds(search));

    search.getContent().stream()
        .forEach(post -> post.addPossImages(postImagesMap.get(post.getId())));

    return new PageImpl<>(toResponses(search), pageable, search.getTotalElements());
  }

  public List<Response> toResponses(Page<PostSearchDto> searchDtos) {
    return searchDtos.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

}
