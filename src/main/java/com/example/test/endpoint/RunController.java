package com.example.test.endpoint;

import com.example.test.dto.RunDTO;
import com.example.test.dto.UserStatisticsDTO;
import com.example.test.service.RunService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/runs")
public class RunController {

    private final RunService runService;

    @PostMapping("/start")
    public RunDTO startRun(
            @RequestParam Long userId,
            @RequestParam double startLatitude,
            @RequestParam double startLongitude,
            @RequestParam LocalDateTime startDatetime) {
        return runService.startRun(userId, startLatitude, startLongitude, startDatetime);
    }

    @PutMapping("/finish")
    public RunDTO finishRun(
            @RequestParam Long userId,
            @RequestParam double finishLatitude,
            @RequestParam double finishLongitude,
            @RequestParam LocalDateTime finishDatetime,
            @RequestParam(required = false) Double distance) {
        return runService.finishRun(userId, finishLatitude, finishLongitude, finishDatetime, distance);

    }

    @GetMapping("/user/{userId}")
    public List<RunDTO> getAllRunsForUser(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDateTime fromDatetime,
            @RequestParam(required = false) LocalDateTime toDatetime) {
        return runService.getAllRunsForUser(userId, fromDatetime, toDatetime);
    }

    @GetMapping("/user/{userId}/statistics")
    public UserStatisticsDTO getUserStatistics(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDateTime fromDatetime,
            @RequestParam(required = false) LocalDateTime toDatetime) {
        return runService.getUserStatistics(userId, fromDatetime, toDatetime);
    }
}
