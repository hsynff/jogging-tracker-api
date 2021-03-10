package com.jogging.tracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataFilterReqDto {
    @NotNull
    @Min(0)
    private Integer page;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer itemsPerPage;

    private String query;

}
