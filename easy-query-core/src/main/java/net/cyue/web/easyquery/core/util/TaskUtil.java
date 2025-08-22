package net.cyue.web.easyquery.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 任务工具类
 */
public class TaskUtil {
    private static final String DEFAULT_TASK = "default";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskUtil.class);

    // 使用线程安全的集合
    private static final Map<String, List<Consumer<Object>>> TASK_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 添加任务到默认任务组
     * @param task 任务
     */
    public static void addTask(Consumer<Object> task) {
        addTask(DEFAULT_TASK, task);
    }

    /**
     * 添加任务到指定任务组
     * @param taskName 任务组名称
     * @param task 任务
     */
    public static void addTask(String taskName, Consumer<Object> task) {
        if (taskName == null || task == null) {
            LOGGER.warn("任务名称或任务不能为空");
            return;
        }

        LOGGER.info("添加任务：{}", taskName);

        // 使用 computeIfAbsent 确保线程安全
        TASK_LIST_MAP.computeIfAbsent(taskName, k -> new CopyOnWriteArrayList<>()).add(task);
    }

    /**
     * 执行默认任务组
     */
    public static void runTask() {
        runTask(DEFAULT_TASK, null);
    }

    /**
     * 执行指定任务组（无参数）
     * @param taskName 任务组名称
     */
    public static void runTask(String taskName) {
        runTask(taskName, null);
    }

    /**
     * 执行指定任务组（带参数）- 每个任务只执行一次
     * @param taskName 任务组名称
     * @param arg 任务执行参数
     */
    public static void runTask(String taskName, Object arg) {
        if (taskName == null) {
            LOGGER.warn("任务名称不能为空");
            return;
        }

        LOGGER.info("执行任务：{}", taskName);

        // 获取并移除任务列表，确保只有一个线程能获取到
        List<Consumer<Object>> taskList = TASK_LIST_MAP.remove(taskName);
        if (taskList == null || taskList.isEmpty()) {
            LOGGER.info("任务组 {} 不存在或为空", taskName);
            return;
        }

        // 执行所有任务，每个任务只执行一次
        for (Consumer<Object> task : taskList) {
            try {
                task.accept(arg);
            } catch (Exception e) {
                LOGGER.error("执行任务时发生异常", e);
            }
        }
    }

    /**
     * 逐个执行任务（每个任务只执行一次）
     * @param taskName 任务名称
     * @param arg 任务执行参数
     */
    public static void runTaskOneByOne(String taskName, Object arg) {
        if (taskName == null) {
            LOGGER.warn("任务名称不能为空");
            return;
        }

        LOGGER.info("逐个执行任务：{}", taskName);

        List<Consumer<Object>> taskList = TASK_LIST_MAP.get(taskName);
        if (taskList == null || taskList.isEmpty()) {
            LOGGER.info("任务组 {} 不存在或为空", taskName);
            return;
        }

        // 逐个取出并执行任务，确保每个任务只执行一次
        Iterator<Consumer<Object>> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            Consumer<Object> task = iterator.next();
            iterator.remove(); // 立即移除，确保只执行一次
            try {
                task.accept(arg);
            } catch (Exception e) {
                LOGGER.error("执行任务时发生异常", e);
            }
        }

        // 如果任务列表为空，移除整个任务组
        if (taskList.isEmpty()) {
            TASK_LIST_MAP.remove(taskName);
        }
    }

    /**
     * 检查任务组是否存在
     * @param taskName 任务组名称
     * @return 任务组存在存在结果
     */
    public static boolean hasTask(String taskName) {
        List<Consumer<Object>> tasks = TASK_LIST_MAP.get(taskName);
        return tasks != null && !tasks.isEmpty();
    }

    /**
     * 获取任务组中的任务数量
     * @param taskName 任务组名称
     * @return 任务数量
     */
    public static int getTaskCount(String taskName) {
        List<Consumer<Object>> tasks = TASK_LIST_MAP.get(taskName);
        return tasks != null ? tasks.size() : 0;
    }

    /**
     * 清除所有任务
     */
    public static void clearAllTasks() {
        int count = TASK_LIST_MAP.size();
        TASK_LIST_MAP.clear();
        LOGGER.info("已清除所有任务，共清除 {} 个任务组", count);
    }

    /**
     * 清除指定任务组
     * @param taskName 任务组名称
     */
    public static void clearTasks(String taskName) {
        List<Consumer<Object>> removed = TASK_LIST_MAP.remove(taskName);
        int count = removed != null ? removed.size() : 0;
        LOGGER.info("已清除任务组：{}，共清除 {} 个任务", taskName, count);
    }
}
