package com.jogging.tracker.controller;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.jogging.tracker.config.AppException.ErrorCodeMsg.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final UserService userService;

    @PutMapping("/users/{id}/images")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #id == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public void uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile multipartFile) throws AppException {
        if (multipartFile.getContentType() == null || !multipartFile.getContentType().startsWith("image/")) {
            throw new AppException(INVALID_FILE_CONTENT_TYPE);
        }

        try {
            userService.addOrUpdateImage(id, multipartFile.getBytes(), multipartFile.getOriginalFilename());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new AppException(INTERNAL);
        }
    }

    @GetMapping(value = "/users/{id}/images", produces = MediaType.IMAGE_JPEG_VALUE)
    @PreAuthorize("(hasRole('ROLE_USER') and #id == authentication.principal.id) or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public byte[] downloadImage(@PathVariable Long id) throws AppException {
        return userService.downloadImage(id);
    }


}
