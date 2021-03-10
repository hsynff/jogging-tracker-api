package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.entity.ImageData;
import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.repository.UserRepository;
import com.jogging.tracker.util.CommonUtils;
import com.jogging.tracker.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 3;

    private final UserRepository userRepository;
    private final RSQLParser rsqlParser;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User create(String email, String firstName, String lastName, String password, User.Role role, User.Status status) throws AppException {

        if (userRepository.existsByEmail(email)) {
            throw new AppException(AppException.ErrorCodeMsg.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(status);
        user.setFailedLoginAttempts(0);

        return userRepository.save(user);
    }

    @Override
    public DataFilterRespDto<UserDto> getAll(String query, Integer page, Integer itemsPerPage) throws AppException {
        Page<User> userPage = null;

        try {

            if (query != null) {
                Specification<User> userSpec = rsqlParser.parse(query).accept(new CustomRsqlVisitor<>());
                userPage = userRepository.findAll(userSpec, PageRequest.of(page, itemsPerPage));
            } else {
                userPage = userRepository.findAll(PageRequest.of(page, itemsPerPage));
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw CommonUtils.translateToAppException(e);
        }

        return new DataFilterRespDto<>(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.get().map(UserDto::fromEntity).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public User getById(Long id) throws AppException {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public User update(Long id, String email, String firstName, String lastName, String password, User.Role role, User.Status status) throws AppException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));

        if (StringUtils.hasText(email)) {
            user.setEmail(email);
        }

        if (StringUtils.hasText(firstName)) {
            user.setFirstName(firstName);
        }

        if (StringUtils.hasText(lastName)) {
            user.setLastName(lastName);
        }

        if (StringUtils.hasText(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (role != null) {
            user.setRole(role);
        }

        if (status != null) {
            if (status == User.Status.ACTIVE) {
                user.setFailedLoginAttempts(0);
            }
            user.setStatus(status);
        }

        return user;
    }

    @Override
    @Transactional
    public User verify(Long id) throws AppException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));

        if (user.getStatus() != User.Status.NEW) {
            throw new AppException(AppException.ErrorCodeMsg.USER_ALREADY_VERIFIED);
        }

        user.setStatus(User.Status.ACTIVE);

        return user;
    }

    @Override
    @Transactional
    public User delete(Long id) throws AppException {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));
        userRepository.delete(user);
        return user;
    }

    @Override
    @Transactional
    public User login(String email, String password) throws AppException {
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_CREDENTIALS_NOT_VALID));


        if (user.getStatus() == User.Status.NEW) {
            throw new AppException(AppException.ErrorCodeMsg.USER_NOT_VERIFIED);

        } else if (user.getStatus() == User.Status.BLOCKED) {
            throw new AppException(AppException.ErrorCodeMsg.USER_BLOCKED);

        } else if (!passwordEncoder.matches(password, user.getPassword())) {

            Integer failedAttempts = Optional.ofNullable(user.getFailedLoginAttempts()).orElse(0);

            if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS - 1) {
                user.setFailedLoginAttempts(0);
                user.setStatus(User.Status.BLOCKED);

            } else {
                user.setFailedLoginAttempts(failedAttempts + 1);
            }

            throw new AppException(AppException.ErrorCodeMsg.USER_CREDENTIALS_NOT_VALID, String.format("Remaining login attempts: %d", MAX_FAILED_LOGIN_ATTEMPTS - (failedAttempts + 1)));
        }


        user.setFailedLoginAttempts(0);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public ImageData addOrUpdateImage(Long idUser, byte[] imageContent, String fileName) throws AppException {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));


        ImageData imageData = user.getImageData();
        if (imageData == null) {
            imageData = new ImageData();
            imageData.setUser(user);
            user.setImageData(imageData);
        }

        imageData.setContent(imageContent);
        imageData.setFileName(fileName);

        userRepository.save(user);

        return imageData;
    }

    @Override
    @Transactional
    public byte[] downloadImage(Long idUser) throws AppException {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));

        return user.getImageData() != null ? user.getImageData().getContent() : null;
    }


}
