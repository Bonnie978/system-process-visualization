#!/bin/bash

# 系统进程可视化Web应用启动脚本

echo "正在启动系统进程监控应用..."

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误: Maven未安装，请先安装Maven"
    exit 1
fi

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: Java未安装，请先安装Java 8或更高版本"
    exit 1
fi

echo "正在编译和启动应用..."

# 使用Maven运行应用
mvn spring-boot:run

echo "应用已启动，访问地址: http://localhost:8081/"