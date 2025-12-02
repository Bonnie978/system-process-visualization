import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;

// 导入ProcessManager类
import com.demo.processmanager.ProcessManager;
import com.demo.processmanager.ProcessInfo;
import com.demo.processmanager.SystemStats;

public class StandaloneServer {
    
    public static void main(String[] args) throws IOException {
        // 创建HTTP服务器，监听8082端口（避免端口冲突）
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        
        // 创建ProcessManager实例
        ProcessManager processManager = new ProcessManager();
        
        // 设置进程列表接口
        server.createContext("/api/processes", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    try {
                        List<ProcessInfo> processes = getRealProcessList(processManager);
                        String response = convertProcessListToJson(processes);
                        
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        
                        System.out.println("返回进程列表: " + processes.size() + " 个进程");
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorResponse = "{\"error\": \"" + e.getMessage() + "\"}";
                        exchange.sendResponseHeaders(500, errorResponse.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(errorResponse.getBytes());
                        os.close();
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            }
        });
        
        // 设置系统状态接口
        server.createContext("/api/system-stats", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    try {
                        SystemStats stats = getRealSystemStats(processManager);
                        String response = convertSystemStatsToJson(stats);
                        
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        
                        System.out.println("返回系统状态: CPU=" + stats.getCpuUsage() + "%, 内存=" + stats.getMemUsage() + "%");
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorResponse = "{\"error\": \"" + e.getMessage() + "\"}";
                        exchange.sendResponseHeaders(500, errorResponse.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(errorResponse.getBytes());
                        os.close();
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            }
        });
        
        // 添加首页处理器，返回简单的HTML页面
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <title>系统进程监控</title>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <h1>系统进程可视化监控</h1>\n" +
                        "    <p>这是一个简化的系统进程监控应用，使用真实系统数据。</p>\n" +
                        "    <h2>可用API接口:</h2>\n" +
                        "    <ul>\n" +
                        "        <li><a href=\"/api/processes\">/api/processes</a> - 获取进程列表</li>\n" +
                        "        <li><a href=\"/api/system-stats\">/api/system-stats</a> - 获取系统状态</li>\n" +
                        "    </ul>\n" +
                        "    <p>点击以上链接查看JSON格式的数据。</p>\n" +
                        "</body>\n" +
                        "</html>";
                
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes("UTF-8"));
                os.close();
            }
        });
        
        // 启动服务器
        server.setExecutor(null); // 使用默认执行器
        server.start();
        
        System.out.println("HTTP服务器已启动，监听端口 8082");
        System.out.println("可用接口:");
        System.out.println("  GET / - 主页");
        System.out.println("  GET /api/processes - 获取进程列表");
        System.out.println("  GET /api/system-stats - 获取系统状态");
        System.out.println("按 Ctrl+C 停止服务器");
    }
    
    // 获取真实的进程列表
    private static List<com.demo.processmanager.ProcessInfo> getRealProcessList(ProcessManager processManager) {
        try {
            // 使用ProcessManager库获取真实进程信息
            return processManager.listProcesses();
        } catch (Exception e) {
            System.err.println("获取真实进程数据失败: " + e.getMessage());
            // 回退到模拟数据
            return getMockProcessList();
        }
    }
    
    // 获取真实的系统状态
    private static com.demo.processmanager.SystemStats getRealSystemStats(ProcessManager processManager) {
        try {
            // 使用ProcessManager库获取真实系统状态
            return processManager.getSystemStats();
        } catch (Exception e) {
            System.err.println("获取真实系统状态失败: " + e.getMessage());
            // 回退到模拟数据
            return getMockSystemStats();
        }
    }
    
    // 模拟进程列表（备用方案）
    private static List<com.demo.processmanager.ProcessInfo> getMockProcessList() {
        return new ArrayList<>();
    }
    
    // 模拟系统状态（备用方案）
    private static com.demo.processmanager.SystemStats getMockSystemStats() {
        return null;
    }
    
    // 将进程列表转换为JSON格式
    private static String convertProcessListToJson(List<com.demo.processmanager.ProcessInfo> processes) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < processes.size(); i++) {
            if (i > 0) json.append(",");
            com.demo.processmanager.ProcessInfo p = processes.get(i);
            json.append("{");
            json.append("\"processName\":\"").append(escapeJsonString(p.getName())).append("\",");
            json.append("\"pid\":").append(p.getPid()).append(",");
            json.append("\"cpuUsage\":").append(p.getCpu()).append(",");
            json.append("\"memoryUsage\":").append(p.getMemory()).append(",");
            json.append("\"startTime\":\"").append(escapeJsonString(p.getStartTime())).append("\"");
            json.append("}");
        }
        
        json.append("]");
        return json.toString();
    }
    
    // 将系统状态转换为JSON格式
    private static String convertSystemStatsToJson(com.demo.processmanager.SystemStats stats) {
        if (stats == null) {
            return "{}";
        }
        
        return "{" +
                "\"cpuUsage\":" + stats.getCpuUsage() + "," +
                "\"memUsage\":" + stats.getMemUsage() + "," +
                "\"jvmHeap\":" + stats.getJvmHeap() + "," +
                "\"jvmMaxHeap\":" + stats.getJvmMaxHeap() + "," +
                "\"jvmHeapUsage\":" + stats.getJvmHeapUsage() +
                "}";
    }
    
    // 简单的JSON字符串转义
    private static String escapeJsonString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}