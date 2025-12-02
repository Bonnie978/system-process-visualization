class ProcessMonitor {
    constructor() {
        this.chart = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadData();
        this.startAutoRefresh();
    }

    setupEventListeners() {
        document.getElementById('refreshBtn').addEventListener('click', () => {
            this.loadData();
        });
    }

    async loadData() {
        try {
            const [processes, stats] = await Promise.all([
                this.fetchProcesses(),
                this.fetchSystemStats()
            ]);
            
            this.updateProcessTable(processes);
            this.updateSystemStats(stats);
            this.updateChart(stats);
        } catch (error) {
            console.error('加载数据失败:', error);
            this.showError('数据加载失败，请检查网络连接');
        }
    }

    async fetchProcesses() {
        const response = await fetch('/api/processes');
        if (!response.ok) throw new Error('获取进程数据失败');
        return await response.json();
    }

    async fetchSystemStats() {
        const response = await fetch('/api/system-stats');
        if (!response.ok) throw new Error('获取系统状态失败');
        return await response.json();
    }

    updateProcessTable(processes) {
        const tbody = document.getElementById('processTableBody');
        tbody.innerHTML = '';

        processes.forEach(process => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${process.name}</td>
                <td>${process.pid}</td>
                <td>${process.cpuUsage.toFixed(1)}%</td>
                <td>${process.memoryUsage.toFixed(1)} MB</td>
            `;
            tbody.appendChild(row);
        });
    }

    updateSystemStats(stats) {
        document.getElementById('cpuUsage').textContent = `${stats.cpuUsage.toFixed(1)}%`;
        document.getElementById('memoryUsage').textContent = `${stats.memoryUsage.toFixed(1)}%`;
        document.getElementById('cpuCores').textContent = stats.cpuCores;
    }

    updateChart(stats) {
        const ctx = document.getElementById('resourceChart').getContext('2d');
        
        if (this.chart) {
            this.chart.destroy();
        }

        this.chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['CPU使用率', '内存使用率'],
                datasets: [{
                    data: [stats.cpuUsage, stats.memoryUsage],
                    backgroundColor: ['#3498db', '#e74c3c'],
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.parsed}%`;
                            }
                        }
                    }
                }
            }
        });
    }

    startAutoRefresh() {
        // 每30秒自动刷新一次
        setInterval(() => {
            this.loadData();
        }, 30000);
    }

    showError(message) {
        // 简单的错误提示实现
        alert(message);
    }
}

// 页面加载完成后初始化监控器
document.addEventListener('DOMContentLoaded', () => {
    new ProcessMonitor();
});