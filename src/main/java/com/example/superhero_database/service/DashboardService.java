package com.example.superhero_database.service;

import com.example.superhero_database.model.DashboardStats;

public interface DashboardService {
    DashboardStats getStatsForUsername(String username);
}
