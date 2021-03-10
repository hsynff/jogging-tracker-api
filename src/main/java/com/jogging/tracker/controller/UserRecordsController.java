package com.jogging.tracker.controller;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterReqDto;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.dto.RecordDto;
import com.jogging.tracker.model.entity.Record;
import com.jogging.tracker.service.RecordService;
import com.jogging.tracker.util.CommonUtils;
import com.jogging.tracker.util.Markers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping(value = "/users/{userId}/records")
@RequiredArgsConstructor
public class UserRecordsController {

    private final RecordService recordService;

    @PostMapping
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #userId == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecordDto> create(@RequestBody @Validated(Markers.CreateRecord.class) RecordDto recordDto, @PathVariable Long userId) throws AppException {
        LocalDate date = CommonUtils.fromDateString(recordDto.getDate());

        if (date.isAfter(LocalDate.now())) {
            throw new AppException(AppException.ErrorCodeMsg.DATE_SLOT_ERROR);
        }

        Record record = recordService.create(
                date,
                recordDto.getDistance(),
                recordDto.getTime(),
                recordDto.getLatitude(),
                recordDto.getLongitude(),
                userId
        );

        return ResponseEntity.ok(RecordDto.fromEntity(record, true, true));
    }

    @GetMapping
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #userId == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<DataFilterRespDto<RecordDto>> getAll(@ModelAttribute @Valid DataFilterReqDto reqDto, @PathVariable Long userId) throws AppException {
        DataFilterRespDto<RecordDto> respDto = recordService.getAll(
                reqDto.getQuery(),
                reqDto.getPage(),
                reqDto.getItemsPerPage(),
                userId
        );

        return ResponseEntity.ok(respDto);
    }


    @GetMapping("/{id}")
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #userId == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecordDto> getOne(@PathVariable("id") Long id, @PathVariable Long userId) throws AppException {
        Record record = recordService.getById(id, userId);

        return ResponseEntity.ok(RecordDto.fromEntity(record, true, true));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #userId == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecordDto> update(@PathVariable("id") Long id, @RequestBody @Validated(Markers.UpdateRecord.class) RecordDto recordDto, @PathVariable Long userId) throws AppException {
        LocalDate date = CommonUtils.fromDateString(recordDto.getDate());

        if (date.isAfter(LocalDate.now())) {
            throw new AppException(AppException.ErrorCodeMsg.DATE_SLOT_ERROR);
        }

        Record updatedRecord = recordService.update(
                id,
                date,
                recordDto.getDistance(),
                recordDto.getTime(),
                recordDto.getLatitude(),
                recordDto.getLongitude(),
                userId
        );

        return ResponseEntity.ok(RecordDto.fromEntity(updatedRecord, false, true));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #userId == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecordDto> delete(@PathVariable("id") Long id, @PathVariable Long userId) throws AppException {
        Record record = recordService.delete(id, userId);

        return ResponseEntity.ok(RecordDto.fromEntity(record, true, true));
    }

}
