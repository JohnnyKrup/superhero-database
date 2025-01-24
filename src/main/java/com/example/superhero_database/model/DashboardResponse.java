package com.example.superhero_database.model;

import com.example.superhero_database.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private UserDTO user;
    private DashboardStats stats;
}