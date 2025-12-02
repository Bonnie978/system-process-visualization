package com.example.service;

import com.example.dto.ProcessInfo;
import com.example.dto.SystemStats;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessService {

    public List<ProcessInfo> getProcessList() {
        List<ProcessInfo> processes = new ArrayList<>();
        
        // 模拟进程数据
        processes.add(new ProcessInfo("java", 1234, 2.5, 512.0));
        processes.add(new ProcessInfo("chrome", 5678, 15.2, 1024.0));
        processes.add(new ProcessInfo("vscode", 9012, 3.1, 768.0));
        processes.add(new ProcessInfo("finder", 3456, 1.2, 256.0));
        processes.add(new ProcessInfo("terminal", 7890, 0.8, 128.0));
        
        return processes;
    }

    public SystemStats getSystemStats() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        // 模拟系统状态数据
        double cpuUsage = Math.random() * 100;
        double memoryUsage = Math.random() * 80 + 20; // 20-100%
        
        return new SystemStats(cpuUsage, memoryUsage, osBean.getAvailableProcessors());
    }
}