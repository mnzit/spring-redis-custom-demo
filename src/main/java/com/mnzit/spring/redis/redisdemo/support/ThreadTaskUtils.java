package com.mnzit.spring.redis.redisdemo.support;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class ThreadTaskUtils {

    private static MdcThreadPoolTaskExecutor taskExecutor = null;

    static {
        taskExecutor = new MdcThreadPoolTaskExecutor();
        // Number of core threads
        taskExecutor.setCorePoolSize(8);
        // Maximum number of threads
        taskExecutor.setMaxPoolSize(64);
        // Maximum queue length
        taskExecutor.setQueueCapacity(1000);
        // Idle time allowed by thread pool maintenance threads (in seconds)
        taskExecutor.setKeepAliveSeconds(120);
        /*
         * Thread pool processing policy for deny tasks(unlimited range available )
         * ThreadPoolExecutor.AbortPolicy: discards the task and throws a RejectedExecutionException exception.
         * ThreadPoolExecutor.DiscardPolicy: also discards the task, but does not throw an exception.
         * ThreadPoolExecutor.DiscardOldestPolicy: discard the task at the top of the queue and try the task again(repeat this procedure）
         * ThreadPoolExecutor.CallerRunsPolicy: the task is handled by the calling thread and discarded if the actuator is closed.
         */
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        taskExecutor.initialize();
    }

    public static void run(Runnable runnable) {
        taskExecutor.execute(runnable);
    }
}
