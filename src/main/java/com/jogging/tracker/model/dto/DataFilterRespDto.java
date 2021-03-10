package com.jogging.tracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataFilterRespDto<T> {
    private Long totalElements;
    private Integer totalPages;
    private List<T> data;
}
