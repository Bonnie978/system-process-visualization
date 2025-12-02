/**
 * ProcessManager库的模拟实现
 * 在实际项目中，这应该是一个外部的JAR库
 */
public class ProcessManager {
    
    /**
     * 获取进程列表（模拟实现）
     */
    public static String[] listProcesses() {
        // 模拟返回进程信息
        return new String[]{
            "Safari,1234,3.21,200,10:23AM",
            "Java,8976,15.88,512,09:11AM", 
            "Finder,456,1.23,150,08:45AM",
            "Dock,789,0.89,80,08:30AM",
            "SystemUIServer,234,2.45,120,08:55AM"
        };
    }
    
    /**
     * 获取系统状态（模拟实现）
     */
    public static String getSystemStats() {
        // 模拟返回系统状态信息
        return "25.67,16384,4096,256,128"; // CPU%,总内存,已用内存,JVM总内存,JVM已用内存
    }
}