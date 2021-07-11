package com.setge.dddpost.domain.member.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.setge.dddpost.domain.member.application.MemberDto;
import com.setge.dddpost.domain.member.application.MemberDto.DetailedSearchCondition;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberService;
import com.setge.dddpost.domain.post.presentation.PostAdminRestController;
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
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"회원 API"})
@RequestMapping("/admin/api/members")
@RequiredArgsConstructor
@RestController
public class MemberAdminRestController {

  private final MemberService memberService;
  private final MemberModelAssembler memberModelAssembler;


  @ApiOperation(value = "회원 조회 및 검색", notes = "searchOption: \n"
      + "이메일: email \n"
      + "닉네임: nickname \n")
  @GetMapping
  public PagedModel<EntityModel<Response>> retrieveMember(
      @Valid DetailedSearchCondition searchCondition,
      @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable,
      PagedResourcesAssembler pagedResourcesAssembler
  ) {
    Page<Response> page = memberService.retrieveMember(searchCondition, pageable);
    return pagedResourcesAssembler.toModel(page, memberModelAssembler);
  }

  @ApiOperation(value = "회원 상세 조회")
  @GetMapping("/{id}")
  public EntityModel<Response> getMember(@PathVariable("id") final Long id) {
    return memberModelAssembler.toModel(memberService.getMember(id));
  }

  @Component
  static class MemberModelAssembler implements
      RepresentationModelAssembler<MemberDto.Response, EntityModel<MemberDto.Response>> {

    @Override
    public EntityModel<MemberDto.Response> toModel(MemberDto.Response entity) {
      return EntityModel.of(entity,
          linkTo(methodOn(MemberAdminRestController.class)
              .getMember(entity.getId())).withSelfRel());
    }

    @Override
    public CollectionModel<EntityModel<MemberDto.Response>> toCollectionModel(
        Iterable<? extends MemberDto.Response> entities) {
      List<EntityModel<MemberDto.Response>> collect = StreamSupport
          .stream(entities.spliterator(), false)
          .map(this::toModel)
          .collect(Collectors.toList());
      return CollectionModel.of(collect);
    }
  }

}
