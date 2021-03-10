package com.jogging.tracker.controller;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterReqDto;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.service.UserService;
import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jogging.tracker.util.Markers.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserDto> create(@RequestBody @Validated(CreateUser.class) UserDto userDto) throws AppException {

        User user = userService.create(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword(),
                User.Role.valueOf(userDto.getRole()),
                User.Status.ACTIVE
        );

        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<DataFilterRespDto<UserDto>> getAll(@ModelAttribute @Valid DataFilterReqDto reqDto) throws AppException {

        DataFilterRespDto<UserDto> respDto = userService.getAll(
                reqDto.getQuery(),
                reqDto.getPage(),
                reqDto.getItemsPerPage()
        );


        return ResponseEntity.ok(respDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_USER') and #id == authentication.principal.id) or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserDto> getOne(@PathVariable("id") Long id) throws AppException {
        User user = userService.getById(id);

        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_USER') and #id == #user.id) or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id, @RequestBody @Validated(UpdateUser.class) UserDto userDto, @AuthenticationPrincipal User user) throws AppException {
        User.Role role = userDto.getRole() != null && user.getRole() != User.Role.ROLE_USER ? User.Role.valueOf(userDto.getRole()) : null;
        User.Status status = userDto.getStatus() != null && user.getRole() != User.Role.ROLE_USER ? User.Status.valueOf(userDto.getStatus()) : null;

        User updatedUser = userService.update(
                id,
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword(),
                role,
                status
        );

        return ResponseEntity.ok(UserDto.fromEntity(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_USER') and #id == authentication.principal.id) or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserDto> delete(@PathVariable("id") Long id) throws AppException {
        User deletedUser = userService.delete(id);

        return ResponseEntity.ok(UserDto.fromEntity(deletedUser));
    }



}
