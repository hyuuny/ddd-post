package com.setge.dddpost.domain.post.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.setge.dddpost.domain.post.application.PostDto.Create;
import com.setge.dddpost.domain.post.application.PostDto.Response;
import com.setge.dddpost.domain.post.application.PostDto.SearchCondition;
import com.setge.dddpost.domain.post.application.PostDto.Update;
import com.setge.dddpost.domain.post.application.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Post API"})
@RequestMapping(value = "/api/posts", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class PostRestController {

  private final PostService postService;
  private final PostModelAssembler postModelAssembler;


  @ApiOperation(value = "게시물 조회 및 검색", notes = "searchOption: \n"
      + "제목: title \n"
      + "닉네임: nickname \n")
  @GetMapping
  public PagedModel<EntityModel<Response>> retrievePost(
      @Valid SearchCondition searchCondition,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable,
      PagedResourcesAssembler pagedResourcesAssembler
  ) {
    Page<Response> page = postService.retrievePost(searchCondition, pageable);
    return pagedResourcesAssembler.toModel(page, postModelAssembler);
  }

  @ApiOperation(value = "게시물 등록")
  @PostMapping
  public EntityModel<Response> createPost(@RequestBody @Valid Create dto) {
    return postModelAssembler.toModel(postService.createPost(dto));
  }

  @ApiOperation(value = "게시물 상세 조회")
  @GetMapping("/{id}")
  public EntityModel<Response> getPost(@PathVariable("id") final Long id) {
    return postModelAssembler.toModel(postService.getPost(id));
  }

  @ApiOperation(value = "게시물 수정")
  @PatchMapping("/{id}")
  public EntityModel<Response> updatePost(
      @PathVariable("id") final Long id,
      @RequestBody @Valid Update dto
  ) {
    return postModelAssembler.toModel(postService.updatePost(id, dto));
  }

  @ApiOperation(value = "게시물 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable("id") final Long id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }


  @Component
  static class PostModelAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(entity,
          linkTo(methodOn(PostRestController.class)
              .getPost(entity.getId())).withSelfRel());
    }

    @Override
    public CollectionModel<EntityModel<Response>> toCollectionModel(
        Iterable<? extends Response> entities) {
      List<EntityModel<Response>> collect = StreamSupport
          .stream(entities.spliterator(), false)
          .map(this::toModel)
          .collect(Collectors.toList());
      return CollectionModel.of(collect);
    }
  }

}
