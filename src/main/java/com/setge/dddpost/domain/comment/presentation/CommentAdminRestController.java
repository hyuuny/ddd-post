package com.setge.dddpost.domain.comment.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.setge.dddpost.domain.comment.application.CommentDto;
import com.setge.dddpost.domain.comment.application.CommentDto.DetailedSearchCondition;
import com.setge.dddpost.domain.comment.application.CommentDto.Response;
import com.setge.dddpost.domain.comment.application.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"댓글 API"})
@RequestMapping(value = "/admin/api/comments", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class CommentAdminRestController {

  private final CommentService commentService;
  private final CommentModelAssembler commentModelAssembler;


  @ApiOperation(value = "댓글 조회 및 검색", notes = "searchOption: \n"
      + "닉네임: nickname \n")
  @GetMapping
  public PagedModel<EntityModel<Response>> retrieveComment(
      @Valid DetailedSearchCondition searchCondition,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable,
      PagedResourcesAssembler<Response> pagedResourcesAssembler
  ) {
    Page<Response> page = commentService.retrieveComment(searchCondition, pageable);
    return pagedResourcesAssembler.toModel(page);
  }

  @ApiOperation(value = "댓글 상세 조회")
  @GetMapping("/{id}")
  public EntityModel<Response> getComment(@PathVariable("id") final Long id) {
    return commentModelAssembler.toModel(commentService.getComment(id));
  }

  @ApiOperation(value = "댓글 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable("id") final Long id) {
    commentService.deleteComment(id);
    return ResponseEntity.noContent().build();
  }


  @Component
  static class CommentModelAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<CommentDto.Response> toModel(CommentDto.Response entity) {
      return EntityModel.of(entity,
          linkTo(methodOn(CommentAdminRestController.class)
              .getComment(entity.getId())).withSelfRel());
    }

    @Override
    public CollectionModel<EntityModel<CommentDto.Response>> toCollectionModel(
        Iterable<? extends CommentDto.Response> entities) {
      List<EntityModel<CommentDto.Response>> collect = StreamSupport
          .stream(entities.spliterator(), false)
          .map(this::toModel)
          .collect(Collectors.toList());
      return CollectionModel.of(collect);
    }
  }

}
