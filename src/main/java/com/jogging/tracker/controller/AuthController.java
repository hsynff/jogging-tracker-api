package com.jogging.tracker.controller;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.InviteDto;
import com.jogging.tracker.service.UserService;
import com.jogging.tracker.config.jwt.JwtTokenProvider;
import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.util.MailSenderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jogging.tracker.util.Markers.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final MailSenderUtil mailSenderUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Validated(Login.class) UserDto dto) throws AppException {
        User loginUser = userService.login(dto.getEmail(), dto.getPassword());
        String token = tokenProvider.generateJwtToken(loginUser);

        return ResponseEntity.ok(token);
    }

    /**
     * I had to declare two separate endpoints for different consume types.
     * Because Spring can not deserialize them in one annotation.
     *
     * @see {https://github.com/spring-projects/spring-framework/issues/22734}
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> register(@RequestBody @Validated(Register.class) UserDto userDto) throws AppException {

        return registerInternal(userDto, null);
    }

    /**
     * I had to declare two separate endpoints for different consume types.
     * Because Spring can not deserialize them in one annotation.
     *
     * @see {https://github.com/spring-projects/spring-framework/issues/22734}
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<UserDto> registerFromInvite(@ModelAttribute @Validated(Register.class) UserDto userDto,
                                                      @RequestParam("inviteToken") String inviteToken) throws AppException {

        return registerInternal(userDto, inviteToken);
    }

    ResponseEntity<UserDto> registerInternal(UserDto userDto, String inviteToken) throws AppException {
        boolean isInvitation = false;

        if (inviteToken != null) {
            if (!tokenProvider.validateToken(inviteToken)) {
                throw new AppException(AppException.ErrorCodeMsg.INVALID_JWT_TOKEN);
            } else if (!tokenProvider.getMailFromInviteToken(inviteToken).equals(userDto.getEmail())) {
                throw new AppException(AppException.ErrorCodeMsg.INVALID_JWT_TOKEN);
            }

            isInvitation = true;
        }

        User user = userService.create(
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword(),
                User.Role.ROLE_USER,
                isInvitation ? User.Status.ACTIVE : User.Status.NEW
        );

        if (!isInvitation) {
            String verifyToken = tokenProvider.generateJwtToken(user);
            mailSenderUtil.sendVerifyMail(user.getEmail(), verifyToken);
        }

        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @GetMapping("/verify")
    public ResponseEntity<UserDto> activate(@RequestParam("token") String token) throws AppException {
        if (!tokenProvider.validateToken(token)) {
            throw new AppException(AppException.ErrorCodeMsg.INVALID_JWT_TOKEN);
        }

        User userFromToken = tokenProvider.createUserFromJwt(token);

        User activeUser = userService.verify(userFromToken.getId());

        return ResponseEntity.ok(UserDto.fromEntity(activeUser));
    }

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void invite(@RequestBody @Valid InviteDto inviteDto) throws AppException {
        if (userService.existsByEmail(inviteDto.getEmail())) {
            throw new AppException(AppException.ErrorCodeMsg.USER_ALREADY_EXISTS);
        }

        String inviteToken = tokenProvider.generateInviteToken(inviteDto.getEmail());
        mailSenderUtil.sendInviteMail(inviteDto.getEmail(), inviteToken);
    }


}
