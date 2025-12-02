# 系统进程可视化Web应用

## 应用概述
这是一个基于Spring Boot的系统进程可视化监控应用，提供实时的系统进程信息和资源使用情况展示。

## 启动方法
```bash
# 使用Maven运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/web-demo-1.0.0.jar
```

## 访问路径
- 主界面: http://localhost:8081/
- API接口:
  - 进程列表: http://localhost:8081/api/processes
  - 系统状态: http://localhost:8081/api/system-stats

## 功能特性
- 实时显示系统进程列表
- 可视化展示CPU和内存使用情况
- 响应式设计，支持不同设备访问
- 手动刷新数据功能

## 技术栈
- Spring Boot 3.4.0
- Thymeleaf模板引擎
- Chart.js图表库
- Java 8

## 注意事项
- 默认端口: 8081
- 如需修改端口，请编辑src/main/resources/application.properties文件

## 故障排除
如果遇到404错误，请确保：
1. 应用已成功启动并在8081端口监听
2. 访问URL正确（http://localhost:8081/）
3. 检查控制台是否有错误信息