package org.RisingSMP.factory.performance;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.RisingSMP.factory.Factory;
import org.RisingSMP.factory.config.FactoryConfig;
import org.RisingSMP.factory.registry.MachineRegistry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;

public class PerformanceManager {
    
    private static ScheduledExecutorService executorService;
    private static final Map<UUID, Long> lastMachineUpdate = new ConcurrentHashMap<>();
    private static final Map<String, Long> performanceMetrics = new ConcurrentHashMap<>();
    private static boolean optimizationEnabled = false;
    
    public static void initialize(JavaPlugin plugin) {
        optimizationEnabled = FactoryConfig.isPerformanceOptimizationEnabled();
        
        if (optimizationEnabled) {
            executorService = Executors.newScheduledThreadPool(4);
            
            // Start performance monitoring
            startPerformanceMonitoring();
            
            // Start cleanup task
            startCleanupTask();
            
            Factory.getInstance().getLogger().info("Performance optimization enabled with " + 
                FactoryConfig.getMaxConcurrentMachines() + " max concurrent machines.");
        }
    }
    
    private static void startPerformanceMonitoring() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Monitor machine count
                int machineCount = MachineRegistry.getAll().size();
                performanceMetrics.put("machine_count", (long) machineCount);
                
                // Monitor memory usage
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                performanceMetrics.put("memory_used", usedMemory);
                
                // Monitor TPS
                double tps = Bukkit.getTPS()[0];
                performanceMetrics.put("tps", (long) (tps * 100));
                
                long processingTime = System.currentTimeMillis() - startTime;
                performanceMetrics.put("monitoring_time", processingTime);
                
                if (FactoryConfig.isVerboseDebugEnabled()) {
                    Factory.getInstance().getLogger().info(String.format(
                        "Performance: %d machines, %.2f TPS, %dms processing time",
                        machineCount, tps, processingTime
                    ));
                }
                
            } catch (Exception e) {
                Factory.getInstance().getLogger().warning("Performance monitoring error: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    
    private static void startCleanupTask() {
        int cleanupInterval = FactoryConfig.getCleanupInterval();
        
        executorService.scheduleAtFixedRate(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Clean up old machine update timestamps
                long currentTime = System.currentTimeMillis();
                lastMachineUpdate.entrySet().removeIf(entry -> 
                    currentTime - entry.getValue() > 60000); // Remove after 1 minute
                
                // Force garbage collection if memory is high
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                long maxMemory = runtime.maxMemory();
                
                if (usedMemory > maxMemory * 0.8) { // If using more than 80% of max memory
                    System.gc();
                    performanceMetrics.put("forced_gc", System.currentTimeMillis());
                }
                
                long cleanupTime = System.currentTimeMillis() - startTime;
                performanceMetrics.put("cleanup_time", cleanupTime);
                
                if (FactoryConfig.isVerboseDebugEnabled()) {
                    Factory.getInstance().getLogger().info("Cleanup completed in " + cleanupTime + "ms");
                }
                
            } catch (Exception e) {
                Factory.getInstance().getLogger().warning("Cleanup task error: " + e.getMessage());
            }
        }, cleanupInterval, cleanupInterval, TimeUnit.MILLISECONDS);
    }
    
    public static boolean canProcessMachine(UUID machineId) {
        if (!optimizationEnabled) {
            return true;
        }
        
        int maxConcurrent = FactoryConfig.getMaxConcurrentMachines();
        int currentProcessing = getCurrentProcessingCount();
        
        return currentProcessing < maxConcurrent;
    }
    
    private static int getCurrentProcessingCount() {
        long currentTime = System.currentTimeMillis();
        return (int) lastMachineUpdate.values().stream()
                .filter(time -> currentTime - time < 1000) // Processing within last second
                .count();
    }
    
    public static void markMachineProcessing(UUID machineId) {
        if (optimizationEnabled) {
            lastMachineUpdate.put(machineId, System.currentTimeMillis());
        }
    }
    
    public static void submitAsyncTask(Runnable task) {
        if (optimizationEnabled && executorService != null) {
            executorService.submit(task);
        } else {
            // Run synchronously if optimization is disabled
            task.run();
        }
    }
    
    public static void scheduleAsyncTask(Runnable task, long delay, TimeUnit unit) {
        if (optimizationEnabled && executorService != null) {
            executorService.schedule(task, delay, unit);
        } else {
            // Run synchronously if optimization is disabled
            task.run();
        }
    }
    
    public static void scheduleRepeatingTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        if (optimizationEnabled && executorService != null) {
            executorService.scheduleAtFixedRate(task, initialDelay, period, unit);
        } else {
            // Run synchronously once if optimization is disabled
            task.run();
        }
    }
    
    // Performance metrics
    public static long getMetric(String metricName) {
        return performanceMetrics.getOrDefault(metricName, 0L);
    }
    
    public static Map<String, Long> getAllMetrics() {
        return new HashMap<>(performanceMetrics);
    }
    
    public static String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("§6=== Performance Report ===\n");
        
        report.append("§7Optimization: ").append(optimizationEnabled ? "§aEnabled" : "§cDisabled").append("\n");
        report.append("§7Max Concurrent Machines: §e").append(FactoryConfig.getMaxConcurrentMachines()).append("\n");
        report.append("§7Current Processing: §e").append(getCurrentProcessingCount()).append("\n");
        
        long machineCount = getMetric("machine_count");
        report.append("§7Total Machines: §e").append(machineCount).append("\n");
        
        long tps = getMetric("tps");
        report.append("§7Server TPS: §e").append(tps / 100.0).append("\n");
        
        long memoryUsed = getMetric("memory_used");
        long memoryMax = Runtime.getRuntime().maxMemory();
        double memoryPercent = (double) memoryUsed / memoryMax * 100;
        report.append("§7Memory Usage: §e").append(String.format("%.1f%%", memoryPercent)).append("\n");
        
        long monitoringTime = getMetric("monitoring_time");
        report.append("§7Monitoring Time: §e").append(monitoringTime).append("ms\n");
        
        long cleanupTime = getMetric("cleanup_time");
        report.append("§7Last Cleanup: §e").append(cleanupTime).append("ms\n");
        
        return report.toString();
    }
    
    // Batch processing for multiple machines
    public static void processBatchMachines(List<Runnable> machineTasks) {
        if (!optimizationEnabled) {
            machineTasks.forEach(Runnable::run);
            return;
        }
        
        int maxConcurrent = FactoryConfig.getMaxConcurrentMachines();
        int batchSize = Math.min(maxConcurrent, machineTasks.size());
        
        // Split into batches and process concurrently
        for (int i = 0; i < machineTasks.size(); i += batchSize) {
            int end = Math.min(i + batchSize, machineTasks.size());
            List<Runnable> batch = machineTasks.subList(i, end);
            
            for (Runnable task : batch) {
                submitAsyncTask(task);
            }
            
            // Small delay between batches to prevent overwhelming
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    // Resource management
    public static void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // Memory optimization
    public static void optimizeMemory() {
        if (!optimizationEnabled) {
            return;
        }
        
        // Clear caches
        lastMachineUpdate.clear();
        
        // Force garbage collection
        System.gc();
        
        performanceMetrics.put("manual_optimization", System.currentTimeMillis());
    }
    
    // Machine-specific optimization
    public static boolean shouldSkipMachineUpdate(UUID machineId, long updateInterval) {
        if (!optimizationEnabled) {
            return false;
        }
        
        Long lastUpdate = lastMachineUpdate.get(machineId);
        if (lastUpdate == null) {
            return false;
        }
        
        return System.currentTimeMillis() - lastUpdate < updateInterval;
    }
}
