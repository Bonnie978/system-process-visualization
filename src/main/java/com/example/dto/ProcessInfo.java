package com.example.dto;

public class ProcessInfo {
    private String name;
    private int pid;
    private double cpuUsage;
    private double memoryUsage;

    public ProcessInfo(String name, int pid, double cpuUsage, double memoryUsage) {
        this.name = name;
        this.pid = pid;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }

    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }

    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
}