package com.example.superhero_database.controller;

import com.example.superhero_database.dto.UserDTO;
import com.example.superhero_database.model.DashboardResponse;
import com.example.superhero_database.model.DashboardStats;
import com.example.superhero_database.service.DashboardService;
import com.example.superhero_database.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserService userService;
    private final DashboardService dashboardService;

    public DashboardController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        // this returns the email not the name
        String username = authentication.getName();
        System.out.println("Dashboard Controller: username => " + username);
        UserDTO userDTO = userService.getUserByEmail(username);

        DashboardStats stats = dashboardService.getStatsForUsername(userDTO.username());
        DashboardResponse response = new DashboardResponse(userDTO, stats);

        return ResponseEntity.ok(response);
    }
}