package com.jogging.tracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
    private String dateFrom;
    private String dateTo;
    private Integer weekOfYear;
    private Double avgSpeed;
    private Double avgDistance;
}
