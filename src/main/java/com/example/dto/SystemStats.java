package com.example.dto;

public class SystemStats {
    private double cpuUsage;
    private double memoryUsage;
    private int cpuCores;

    public SystemStats(double cpuUsage, double memoryUsage, int cpuCores) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.cpuCores = cpuCores;
    }

    // Getters and Setters
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }

    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }

    public int getCpuCores() { return cpuCores; }
    public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }
}