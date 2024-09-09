package com.example.test.service.impl;

import com.example.test.dto.RunDTO;
import com.example.test.dto.UserStatisticsDTO;
import com.example.test.mapper.RunMapper;
import com.example.test.model.Run;
import com.example.test.repository.RunRepository;
import com.example.test.service.RunService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RunServiceImpl implements RunService {

    private final RunRepository runRepository;

    private final RunMapper runMapper;

    @Override
    public RunDTO startRun(Long userId, Double startLatitude, Double startLongitude, LocalDateTime startDatetime) {
        Run run = new Run();
        run.setUserId(userId);
        run.setStartLatitude(startLatitude);
        run.setStartLongitude(startLongitude);
        run.setStartDatetime(startDatetime);
        run = runRepository.save(run);

        return runMapper.toRunDTO(run);
    }

    @Override
    public RunDTO finishRun(Long userId, Double finishLatitude, Double finishLongitude, LocalDateTime finishDatetime, Double distance) {
        Run run = runRepository.findTopByUserIdOrderByStartDatetimeDesc(userId)
                .orElseThrow(() -> new RuntimeException("No ongoing run found for user id: " + userId));

        run.setFinishLatitude(finishLatitude);
        run.setFinishLongitude(finishLongitude);
        run.setFinishDatetime(finishDatetime);

        if (distance == null) {
            distance = calculateDistance(run.getStartLatitude(), run.getStartLongitude(), finishLatitude, finishLongitude);
        }
        run.setDistance(distance);

        double durationInHours = (double) Duration.between(run.getStartDatetime(), finishDatetime).toMillis() / 1000 / 3600;
        run.setAverageSpeed(distance / durationInHours);

        run = runRepository.save(run);
        return runMapper.toRunDTO(run);
    }

    @Override
    public List<RunDTO> getAllRunsForUser(Long userId, LocalDateTime fromDatetime, LocalDateTime toDatetime) {
        List<Run> runs = runRepository.findAllByUserIdAndStartDatetimeBetween(userId, fromDatetime, toDatetime);
        return runs.stream().map(runMapper::toRunDTO).collect(Collectors.toList());
    }

    @Override
    public UserStatisticsDTO getUserStatistics(Long userId, LocalDateTime fromDatetime, LocalDateTime toDatetime) {
        List<Run> runs = runRepository.findAllByUserIdAndStartDatetimeBetween(userId, fromDatetime, toDatetime);

        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setTotalRuns(runs.size());
        stats.setTotalDistance(runs.stream().mapToDouble(Run::getDistance).sum());

        double totalDurationInHours = runs.stream()
                .mapToDouble(run -> (double) Duration.between(run.getStartDatetime(), run.getFinishDatetime()).toMillis() / 1000 / 3600)
                .sum();

        stats.setAverageSpeed(stats.getTotalDistance() / totalDurationInHours);

        return stats;
    }

    private double calculateDistance(Double startLatitude, Double startLongitude, Double finishLatitude, Double finishLongitude) {
        final int R = 6371;

        double latDistance = Math.toRadians(finishLatitude - startLatitude);
        double lonDistance = Math.toRadians(finishLongitude - startLongitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(startLatitude)) * Math.cos(Math.toRadians(finishLatitude)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000;
    }
}
