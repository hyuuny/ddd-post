package com.setge.dddpost.domain.nestedcomment.presentation;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Create;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Response;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentDto.Update;
import com.setge.dddpost.domain.nestedcomment.application.NestedCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
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

@Api(tags = {"대댓글 API"})
@RequestMapping(value = "/api/nested-comments", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class NestedCommentRestController {

  private final NestedCommentService nestedCommentService;
  private final NestedCommentModelAssembler nestedCommentModelAssembler;


  @ApiOperation(value = "대댓글 등록")
  @PostMapping("/{id}/register/nested")
  public EntityModel<Response> createNestedComment(
      @PathVariable("id") final Long commentId,
      @RequestBody @Valid Create dto
  ) {
    return nestedCommentModelAssembler.toModel(nestedCommentService.createNestedComment(commentId, dto));
  }

  @ApiOperation(value = "대댓글 상세 조회")
  @GetMapping("/{id}")
  public EntityModel<Response> getNestedComment(@PathVariable("id") final Long id) {
    return nestedCommentModelAssembler.toModel(nestedCommentService.getNestedComment(id));
  }

  @ApiOperation(value = "대댓글 수정")
  @PatchMapping("/{id}")
  public EntityModel<Response> updateNestedComment(
      @PathVariable("id") final Long id,
      @RequestBody @Valid Update dto
  ) {
    return nestedCommentModelAssembler.toModel(nestedCommentService.updateNestedComment(id, dto));
  }

  @ApiOperation(value = "대댓글 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteNestedComment(@PathVariable("id") final Long id) {
    nestedCommentService.deleteNestedComment(id);
    return ResponseEntity.noContent().build();
  }

  @Component
  static class NestedCommentModelAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(entity,
          linkTo(methodOn(NestedCommentRestController.class)
              .getNestedComment(entity.getId())).withSelfRel());
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
