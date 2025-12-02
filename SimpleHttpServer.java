import com.example.service.ProcessService;
import com.example.dto.ProcessInfo;
import com.example.dto.SystemStats;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SimpleHttpServer {
    private static ProcessService processService = new ProcessService();
    
    public static void main(String[] args) throws IOException {
        // 创建HTTP服务器，监听8081端口
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // 设置进程列表接口
        server.createContext("/api/processes", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    try {
                        List<ProcessInfo> processes = processService.getProcessList();
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
                        SystemStats stats = processService.getSystemStats();
                        String response = convertSystemStatsToJson(stats);
                        
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        
                        System.out.println("返回系统状态: CPU=" + stats.getCpuUsage() + "%, 内存=" + stats.getUsedMemory() + "MB");
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
        
        // 添加静态资源处理器
        server.createContext("/css", new StaticFileHandler("src/main/resources/static"));
        server.createContext("/js", new StaticFileHandler("src/main/resources/static"));
        server.createContext("/favicon.ico", new StaticFileHandler("src/main/resources/static"));
        
        // 添加首页处理器，返回Thymeleaf模板渲染的HTML页面
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("/".equals(exchange.getRequestURI().getPath())) {
                    // 读取模板文件
                    Path templatePath = Paths.get("src/main/resources/templates/process.html");
                    String templateContent = new String(Files.readAllBytes(templatePath), "UTF-8");
                    
                    // 简单替换Thymeleaf标签
                    String response = templateContent
                            .replace("th:href=\"@{/css/style.css}\"", "href=\"/css/style.css\"")
                            .replace("th:src=\"@{/js/app.js}\"", "src=\"/js/app.js\"")
                            .replace("th:href=\"@{/favicon.ico}\"", "href=\"/favicon.ico\"");
                    
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                    exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes("UTF-8"));
                    os.close();
                } else {
                    // 对于其他静态资源请求，交给StaticFileHandler处理
                    String response = "Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        });
        
        // 启动服务器
        server.setExecutor(null); // 使用默认执行器
        server.start();
        
        System.out.println("HTTP服务器已启动，监听端口 8081");
        System.out.println("可用接口:");
        System.out.println("  GET / - 主页");
        System.out.println("  GET /api/processes - 获取进程列表");
        System.out.println("  GET /api/system-stats - 获取系统状态");
        System.out.println("按 Ctrl+C 停止服务器");
    }
    
    // 将进程列表转换为JSON格式
    private static String convertProcessListToJson(List<ProcessInfo> processes) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < processes.size(); i++) {
            if (i > 0) json.append(",");
            ProcessInfo p = processes.get(i);
            json.append("{");
            json.append("\"processName\":\"").append(escapeJsonString(p.getProcessName())).append("\",");
            json.append("\"pid\":").append(p.getPid()).append(",");
            json.append("\"cpuUsage\":").append(p.getCpuUsage()).append(",");
            json.append("\"memoryUsage\":").append(p.getMemoryUsage()).append(",");
            json.append("\"startTime\":\"").append(escapeJsonString(p.getStartTime())).append("\"");
            json.append("}");
        }
        
        json.append("]");
        return json.toString();
    }
    
    // 将系统状态转换为JSON格式
    private static String convertSystemStatsToJson(SystemStats stats) {
        return "{" +
                "\"cpuUsage\":" + stats.getCpuUsage() + "," +
                "\"totalMemory\":" + stats.getTotalMemory() + "," +
                "\"usedMemory\":" + stats.getUsedMemory() + "," +
                "\"jvmTotalMemory\":" + stats.getJvmTotalMemory() + "," +
                "\"jvmUsedMemory\":" + stats.getJvmUsedMemory() +
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
    
    // 静态文件处理器类
    static class StaticFileHandler implements HttpHandler {
        private final String basePath;
        
        public StaticFileHandler(String basePath) {
            this.basePath = basePath;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // 获取请求路径
                String requestPath = exchange.getRequestURI().getPath();
                
                // 构建文件路径
                Path filePath = Paths.get(basePath, requestPath.substring(1));
                
                // 检查文件是否存在
                if (!Files.exists(filePath)) {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    return;
                }
                
                // 检查是否为目录
                if (Files.isDirectory(filePath)) {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    return;
                }
                
                // 确定内容类型
                String contentType = getContentType(filePath.toString());
                
                // 读取文件内容
                byte[] fileContent = Files.readAllBytes(filePath);
                
                // 发送响应
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileContent.length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(fileContent);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                String response = "500 Internal Server Error";
                exchange.sendResponseHeaders(500, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        
        private String getContentType(String fileName) {
            if (fileName.endsWith(".css")) {
                return "text/css";
            } else if (fileName.endsWith(".js")) {
                return "application/javascript";
            } else if (fileName.endsWith(".html")) {
                return "text/html; charset=utf-8";
            } else if (fileName.endsWith(".ico")) {
                return "image/x-icon";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else {
                return "application/octet-stream";
            }
        }
    }
}