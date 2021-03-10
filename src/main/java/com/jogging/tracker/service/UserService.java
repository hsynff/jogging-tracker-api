package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.entity.ImageData;
import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.User;

public interface UserService {
    User create(String email, String firstName, String lastName, String password, User.Role role, User.Status status) throws AppException;

    DataFilterRespDto<UserDto> getAll(String query, Integer page, Integer itemsPerPage) throws AppException;

    User getById(Long id) throws AppException;

    User update(Long id, String email, String firstName, String lastName, String password, User.Role role, User.Status status) throws AppException;

    User verify(Long id) throws AppException;

    User delete(Long id) throws AppException;

    User login(String email, String password) throws AppException;

    boolean existsByEmail(String email);

    ImageData addOrUpdateImage(Long idUser, byte[] imageContent, String fileName) throws AppException;

    byte[] downloadImage(Long userId) throws AppException;
}
