package com.example.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunDTO {
    private Long id;
    private Long userId;
    private Double startLatitude;
    private Double startLongitude;
    private LocalDateTime startDatetime;
    private Double finishLatitude;
    private Double finishLongitude;
    private LocalDateTime finishDatetime;
    private Double distance;
    private Double averageSpeed;

}
   
