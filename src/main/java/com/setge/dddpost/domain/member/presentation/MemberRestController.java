package com.setge.dddpost.domain.member.presentation;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.setge.dddpost.domain.member.application.MemberDto;
import com.setge.dddpost.domain.member.application.MemberDto.CheckEmail;
import com.setge.dddpost.domain.member.application.MemberDto.CheckNickname;
import com.setge.dddpost.domain.member.application.MemberDto.Join;
import com.setge.dddpost.domain.member.application.MemberDto.Response;
import com.setge.dddpost.domain.member.application.MemberDto.Update;
import com.setge.dddpost.domain.member.application.MemberService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Member API"})
@RequestMapping(value = "/api/members", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
@RestController
public class MemberRestController {

  private final MemberService memberService;
  private final MemberModelAssembler memberModelAssembler;


  @ApiOperation(value = "회원 가입")
  @PostMapping
  public EntityModel<Response> joinMember(@RequestBody @Valid Join dto) {
    return memberModelAssembler.toModel(memberService.joinMember(dto));
  }

  @ApiOperation(value = "이메일 중복 체크")
  @PostMapping("/check/email")
  public ResponseEntity<?> checkEmail(@RequestBody @Valid CheckEmail dto) {
    memberService.checkEmail(dto);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "닉네임 중복 체크")
  @PostMapping("/check/nickname")
  public ResponseEntity<?> checkNickname(@RequestBody @Valid CheckNickname dto) {
    memberService.checkNickname(dto);
    return ResponseEntity.noContent().build();
  }

  @ApiOperation(value = "회원 상세 조회")
  @GetMapping("/{id}")
  public EntityModel<MemberDto.Response> getMember(@PathVariable("id") final Long id) {
    return memberModelAssembler.toModel(memberService.getMember(id));
  }

  @ApiOperation(value = "회원 수정")
  @PatchMapping("/{id}")
  public EntityModel<Response> updateMember(
      @PathVariable("id") final Long id,
      @RequestBody @Valid Update dto
  ) {
    return memberModelAssembler.toModel(memberService.updateMember(id, dto));
  }

  @ApiOperation(value = "회원 탈퇴")
  @PostMapping("/leave/{id}")
  public ResponseEntity<?> leaveMember(@PathVariable("id") final Long id) {
    memberService.leaveMember(id);
    return ResponseEntity.noContent().build();
  }


  @Component
  static class MemberModelAssembler implements
      RepresentationModelAssembler<MemberDto.Response, EntityModel<MemberDto.Response>> {

    @Override
    public EntityModel<MemberDto.Response> toModel(MemberDto.Response entity) {
      return EntityModel.of(entity,
          linkTo(methodOn(MemberRestController.class)
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
