package com.example.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.example.test.dto.RunDTO;
import com.example.test.dto.UserStatisticsDTO;
import com.example.test.mapper.RunMapper;
import com.example.test.model.Run;
import com.example.test.repository.RunRepository;
import com.example.test.service.impl.RunServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RunServiceTest {

    @InjectMocks
    private RunServiceImpl runService;

    @Mock
    private RunRepository runRepository;

    @Mock
    private RunMapper runMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startRun_ShouldReturnRunDTO() {
        Run run = new Run();
        run.setUserId(1L);
        run.setStartLatitude(12.34);
        run.setStartLongitude(56.78);
        run.setStartDatetime(LocalDateTime.now());

        RunDTO runDTO = new RunDTO();
        runDTO.setUserId(1L);
        runDTO.setStartLatitude(12.34);
        runDTO.setStartLongitude(56.78);
        runDTO.setStartDatetime(LocalDateTime.now());

        when(runRepository.save(any(Run.class))).thenReturn(run);
        when(runMapper.toRunDTO(run)).thenReturn(runDTO);

        RunDTO savedRunDTO = runService.startRun(1L, 12.34, 56.78, LocalDateTime.now());

        assertThat(savedRunDTO.getUserId()).isEqualTo(1L);
        verify(runRepository, times(1)).save(any(Run.class));
    }

    @Test
    void finishRun_ShouldReturnRunDTO() {
        Run run = new Run();
        run.setId(1L);
        run.setUserId(1L);
        run.setStartLatitude(12.34);
        run.setStartLongitude(56.78);
        run.setStartDatetime(LocalDateTime.now());

        RunDTO runDTO = new RunDTO();
        runDTO.setUserId(1L);
        runDTO.setFinishLatitude(90.12);
        runDTO.setFinishLongitude(34.56);
        runDTO.setFinishDatetime(LocalDateTime.now().plusMinutes(30));
        runDTO.setDistance(1000.0);

        when(runRepository.findById(1L)).thenReturn(Optional.of(run));
        when(runRepository.save(any(Run.class))).thenReturn(run);
        when(runMapper.toRunDTO(run)).thenReturn(runDTO);
        when(runRepository.findTopByUserIdOrderByStartDatetimeDesc(1L)).thenReturn(Optional.of(run));
        RunDTO finishedRunDTO = runService.finishRun(1L, 90.12, 34.56, LocalDateTime.now().plusMinutes(30), 1000.0);

        assertThat(finishedRunDTO.getDistance()).isEqualTo(1000.0);
        verify(runRepository, times(1)).save(any(Run.class));
    }

    @Test
    void getUserStatistics_ShouldReturnUserStatistics() {
        Long userId = 1L;
        LocalDateTime fromDatetime = LocalDateTime.now().minusDays(2);
        LocalDateTime toDatetime = LocalDateTime.now();

        Run run1 = new Run();
        run1.setUserId(userId);
        run1.setStartDatetime(LocalDateTime.now().minusHours(2));
        run1.setFinishDatetime(LocalDateTime.now().minusHours(1));
        run1.setDistance(5.0);

        Run run2 = new Run();
        run2.setUserId(userId);
        run2.setStartDatetime(LocalDateTime.now().minusDays(1).minusHours(2));
        run2.setFinishDatetime(LocalDateTime.now().minusDays(1).minusHours(1));
        run2.setDistance(10.0);

        List<Run> runs = Arrays.asList(run1, run2);

        when(runRepository.findAllByUserIdAndStartDatetimeBetween(userId, fromDatetime, toDatetime)).thenReturn(runs);

        UserStatisticsDTO stats = runService.getUserStatistics(userId, fromDatetime, toDatetime);

        assertThat(stats.getTotalRuns()).isEqualTo(2);
        assertThat(stats.getTotalDistance()).isEqualTo(15.0);


        double totalDurationInHours = (double) Duration.between(run1.getStartDatetime(), run1.getFinishDatetime()).toMillis() / 1000 / 3600
                + (double) Duration.between(run2.getStartDatetime(), run2.getFinishDatetime()).toMillis() / 1000 / 3600;
        assertThat(stats.getAverageSpeed()).isEqualTo(15.0 / totalDurationInHours);

        verify(runRepository, times(1)).findAllByUserIdAndStartDatetimeBetween(userId, fromDatetime, toDatetime);
    }
}
