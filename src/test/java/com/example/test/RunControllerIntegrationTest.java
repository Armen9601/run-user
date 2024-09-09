package com.example.test;

import com.example.test.model.Run;
import com.example.test.model.Users;
import com.example.test.repository.RunRepository;
import com.example.test.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RunControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RunRepository runRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        runRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void startRun_ShouldReturnCreatedRun() throws Exception {
        // Create and save a user in the database
        Users user = new Users();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setSex("Male");
        user = userRepository.save(user);

        // Prepare the query parameters
        Long userId = user.getId();
        double startLatitude = 12.34;
        double startLongitude = 56.78;
        LocalDateTime startDatetime = LocalDateTime.now();

        // Perform the POST request with query parameters
        mockMvc.perform(post("/api/runs/start")
                        .param("userId", userId.toString())
                        .param("startLatitude", String.valueOf(startLatitude))
                        .param("startLongitude", String.valueOf(startLongitude))
                        .param("startDatetime", startDatetime.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startLatitude", is(startLatitude)));
    }

    @Test
    void finishRun_ShouldReturnUpdatedRun() throws Exception {
        Users user = new Users();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setSex("Male");
        user = userRepository.save(user);

        Run run = new Run();
        run.setUserId(user.getId());
        run.setStartLatitude(12.34);
        run.setStartLongitude(56.78);
        run.setStartDatetime(LocalDateTime.now());
        run = runRepository.save(run);

        // Prepare the query parameters
        Long userId = user.getId();
        double finishLatitude = 90.12;
        double finishLongitude = 34.56;
        LocalDateTime finishDatetime = LocalDateTime.now().plusMinutes(30);
        double distance = 1000.0;

        mockMvc.perform(put("/api/runs/finish")
                        .param("userId", userId.toString())
                        .param("finishLatitude", String.valueOf(finishLatitude))
                        .param("finishLongitude", String.valueOf(finishLongitude))
                        .param("finishDatetime", finishDatetime.toString())
                        .param("distance", String.valueOf(distance))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finishLatitude", is(finishLatitude)))
                .andExpect(jsonPath("$.distance", is(distance)));
    }


    @Test
    void getAllRunsForUser_ShouldReturnListOfRuns() throws Exception {
        Users user = new Users();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setSex("Male");
        user = userRepository.save(user);

        Run run1 = new Run();
        run1.setUserId(user.getId());
        run1.setStartLatitude(12.34);
        run1.setStartLongitude(56.78);
        run1.setStartDatetime(LocalDateTime.now());
        runRepository.save(run1);

        Run run2 = new Run();
        run2.setUserId(user.getId());
        run2.setStartLatitude(23.45);
        run2.setStartLongitude(67.89);
        run2.setStartDatetime(LocalDateTime.now().plusDays(1));
        runRepository.save(run2);

        mockMvc.perform(get("/api/runs/user/{userId}", user.getId())
                        .param("fromDatetime", LocalDateTime.now().minusDays(1).toString())
                        .param("toDatetime", LocalDateTime.now().plusDays(2).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void getUserStatistics_ShouldReturnUserStatistics() throws Exception {
        // Create and save a user in the database
        Users user = new Users();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setSex("Male");
        user = userRepository.save(user);

        // Create and save runs in the database
        Run run1 = new Run();
        run1.setUserId(user.getId());
        run1.setStartLatitude(12.34);
        run1.setStartLongitude(56.78);
        run1.setStartDatetime(LocalDateTime.now().minusHours(2));
        run1.setFinishDatetime(LocalDateTime.now().minusHours(1));
        run1.setDistance(5.0);
        runRepository.save(run1);

        Run run2 = new Run();
        run2.setUserId(user.getId());
        run2.setStartLatitude(23.45);
        run2.setStartLongitude(67.89);
        run2.setStartDatetime(LocalDateTime.now().minusDays(1).minusHours(2));
        run2.setFinishDatetime(LocalDateTime.now().minusDays(1).minusHours(1));
        run2.setDistance(10.0);
        runRepository.save(run2);

        // Perform the GET request with query parameters
        mockMvc.perform(get("/api/runs/user/{userId}/statistics", user.getId())
                        .param("fromDatetime", LocalDateTime.now().minusDays(2).toString())
                        .param("toDatetime", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRuns", is(2)))
                .andExpect(jsonPath("$.totalDistance", is(15.0)))
                .andExpect(jsonPath("$.averageSpeed", is(7.5))); // Assuming average speed is calculated correctly
    }

}
