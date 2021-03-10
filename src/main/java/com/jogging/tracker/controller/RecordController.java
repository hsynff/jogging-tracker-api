package com.jogging.tracker.controller;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterReqDto;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.entity.Record;
import com.jogging.tracker.service.RecordService;
import com.jogging.tracker.model.dto.RecordDto;
import com.jogging.tracker.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

import static com.jogging.tracker.util.Markers.*;

@RestController
@RequestMapping(value = "/records")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<DataFilterRespDto<RecordDto>> getAll(@ModelAttribute @Valid DataFilterReqDto reqDto) throws AppException {
        DataFilterRespDto<RecordDto> respDto = recordService.getAll(
                reqDto.getQuery(),
                reqDto.getPage(),
                reqDto.getItemsPerPage(),
                null
        );

        return ResponseEntity.ok(respDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RecordDto> getOne(@PathVariable("id") Long id) throws AppException {
        Record record = recordService.getById(id, null);

        return ResponseEntity.ok(RecordDto.fromEntity(record, true, true));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<RecordDto> update(@PathVariable("id") Long id, @RequestBody @Validated(UpdateRecord.class) RecordDto recordDto) throws AppException {
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
                null
        );

        return ResponseEntity.ok(RecordDto.fromEntity(updatedRecord, false, true));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<RecordDto> delete(@PathVariable("id") Long id) throws AppException {
        Record record = recordService.delete(id, null);

        return ResponseEntity.ok(RecordDto.fromEntity(record, true, true));
    }
}
