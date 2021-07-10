package com.setge.dddpost.domain.nestedcomment.presentation;

import com.setge.dddpost.domain.nestedcomment.application.NestedCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"대댓글 API"})
@RequestMapping(value = "/admin/api/nested-comments", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class NestedCommentAdminRestController {

  private final NestedCommentService nestedCommentService;

  @ApiOperation(value = "대댓글 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable("id") final Long id) {
    nestedCommentService.deleteNestedComment(id);
    return ResponseEntity.noContent().build();
  }

}
