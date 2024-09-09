package com.example.test.service;

import com.example.test.dto.RunDTO;
import com.example.test.dto.UserStatisticsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface RunService {
    RunDTO startRun(Long userId, Double startLatitude, Double startLongitude, LocalDateTime startDatetime);
    RunDTO finishRun(Long userId, Double finishLatitude, Double finishLongitude, LocalDateTime finishDatetime, Double distance);
    List<RunDTO> getAllRunsForUser(Long userId, LocalDateTime fromDatetime, LocalDateTime toDatetime);
    UserStatisticsDTO getUserStatistics(Long userId, LocalDateTime fromDatetime, LocalDateTime toDatetime);
}
