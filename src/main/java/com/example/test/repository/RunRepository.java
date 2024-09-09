package com.example.test.repository;

import com.example.test.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {
    
    Optional<Run> findTopByUserIdOrderByStartDatetimeDesc(Long userId);

    List<Run> findAllByUserIdAndStartDatetimeBetween(Long userId, LocalDateTime fromDatetime, LocalDateTime toDatetime);

    @Query("SELECT r FROM Run r WHERE r.userId = :userId AND r.startDatetime BETWEEN :fromDatetime AND :toDatetime ORDER BY r.startDatetime ASC")
    List<Run> findRunsByUserAndDateRange(@Param("userId") Long userId, @Param("fromDatetime") LocalDateTime fromDatetime, @Param("toDatetime") LocalDateTime toDatetime);
}
