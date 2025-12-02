package com.example.controller;

import com.example.dto.ProcessInfo;
import com.example.dto.SystemStats;
import com.example.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @GetMapping("/api/processes")
    public List<ProcessInfo> getProcesses() {
        return processService.getProcessList();
    }

    @GetMapping("/api/system-stats")
    public SystemStats getSystemStats() {
        return processService.getSystemStats();
    }
}