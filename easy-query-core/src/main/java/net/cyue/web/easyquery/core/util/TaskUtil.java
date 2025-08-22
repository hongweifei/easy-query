package net.cyue.web.easyquery.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 任务工具类
 */
public class TaskUtil {

    /**
     * 任务
     * @param <T> 运行参数类型
     */
    @FunctionalInterface
    public interface Task<T> {
        void run(T t) throws Exception;
    }

    /**
     * 任务类型
     * @param <T> 运行参数类型
     */
    public static class TaskType<T> {

        protected final String name;

        public TaskType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.getName();
        }

        public String getName() {
            return "TaskType@" + this.name;
        }
        public String getName(String id) {
            return id + "@" + this.name;
        }
        public String getName(Object obj) {
            return obj + "@" + this.name;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskUtil.class);
    /// 使用线程安全的集合
    private static final Map<TaskType<?>, List<Task<Object>>> TASK_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 添加任务到指定任务组
     * @param <T> 参数类型
     * @param taskType 任务组类型
     * @param task 任务
     */
    public static <T> void addTask(TaskType<T> taskType, Task<T> task) {
        if (taskType == null || task == null) {
            LOGGER.warn("任务类型或任务不能为空");
            return;
        }

        LOGGER.info("添加任务：{}", taskType);

        // 使用 computeIfAbsent 确保线程安全
        TASK_LIST_MAP.computeIfAbsent(taskType, k -> new CopyOnWriteArrayList<>()).add((Task<Object>) task);
    }

    /**
     * 执行指定任务组（带参数）- 每个任务只执行一次
     * @param taskType 任务组类型
     * @param arg 任务执行参数
     * @return 任务是否执行成功
     */
    public static <T> boolean runTask(TaskType<T> taskType, T arg) {
        if (taskType == null) {
            LOGGER.warn("任务类型不能为空");
            return false;
        }

        LOGGER.info("执行任务：{}", taskType);

        // 获取并移除任务列表，确保只有一个线程能获取到
        List<Task<Object>> taskList = TASK_LIST_MAP.remove(taskType);
        if (taskList == null || taskList.isEmpty()) {
            LOGGER.info("任务组 {} 不存在或为空", taskType);
            return false;
        }

        // 执行所有任务，每个任务只执行一次
        for (Task<Object> task : taskList) {
            try {
                task.run(arg);
            } catch (Exception e) {
                LOGGER.error("执行任务时发生异常", e);
                return false;
            }
        }
        return true;
    }

    /**
     * 逐个执行任务（每个任务只执行一次）
     * @param taskType 任务类型
     * @param arg 任务执行参数
     * @return 任务是否执行成功
     */
    public static <T> boolean runTaskOneByOne(TaskType<T> taskType, T arg) {
        if (taskType == null) {
            LOGGER.warn("任务类型不能为空");
            return false;
        }

        LOGGER.info("逐个执行任务：{}", taskType);

        List<Task<Object>> taskList = TASK_LIST_MAP.get(taskType);
        if (taskList == null || taskList.isEmpty()) {
            LOGGER.info("任务组 {} 不存在或为空", taskType);
            return false;
        }

        // 逐个取出并执行任务，确保每个任务只执行一次
        Iterator<Task<Object>> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            Task<Object> task = iterator.next();
            iterator.remove(); // 立即移除，确保只执行一次
            try {
                task.run(arg);
            } catch (Exception e) {
                LOGGER.error("执行任务时发生异常", e);
                return false;
            }
        }

        // 如果任务列表为空，移除整个任务组
        if (taskList.isEmpty()) {
            TASK_LIST_MAP.remove(taskType);
        }

        return true;
    }


    /**
     * 检查任务组是否存在
     * @param taskType 任务组类型
     * @return 任务组存在存在结果
     */
    public static boolean hasTask(TaskType<?> taskType) {
        List<Task<Object>> tasks = TASK_LIST_MAP.get(taskType);
        return tasks != null && !tasks.isEmpty();
    }

    /**
     * 获取任务组中的任务数量
     * @param taskType 任务组类型
     * @return 任务数量
     */
    public static int getTaskCount(TaskType<?> taskType) {
        List<Task<Object>> tasks = TASK_LIST_MAP.get(taskType);
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
     * @param taskType 任务组类型
     */
    public static void clearTasks(TaskType<?> taskType) {
        List<Task<Object>> removed = TASK_LIST_MAP.remove(taskType);
        int count = removed != null ? removed.size() : 0;
        LOGGER.info("已清除任务组：{}，共清除 {} 个任务", taskType, count);
    }
}
