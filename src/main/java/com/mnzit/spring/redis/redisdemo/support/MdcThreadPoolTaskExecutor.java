package com.mnzit.spring.redis.redisdemo.support;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

/**
 * This is a simple replacement for{@link ThreadPoolTaskExecutor}to set the MDC data of the child thread before each task.
 * <p>
 * When logging, in general, we will use MDC to store each thread-specific parameters, such as identity information, etc., in order to better query the log.
 * However, Logback does not automatically pass MDC memory to child threads due to performance issues in the latest version.So Logback recommends before executing asynchronous threads
 * First through MDC.the getCopyOfContextMap () method fetches MDC memory and passes it to the thread.
 * And call MDC at the very beginning of the execution of the child thread.the setContextMap (context) method passes the MDC content of the parent thread to the child thread.
 * </p>
 * https://logback.qos.ch/manual/mdc.html
 */
public class MdcThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    /**
     * All threads are delegated to this execute method, where we assign the MDC content of the parent thread to the child thread
     * https://logback.qos.ch/manual/mdc.html#managedThreads
     *
     * @param runnable runnable
     */
    @Override
    public void execute(Runnable runnable) {
    // Get the contents of the parent thread MDC, must be in front of the run method, otherwise it is possible when the asynchronous thread execution MDC inside the value has been cleared, this time will return null
        Map<String, String> context = MDC.getCopyOfContextMap();
        super.execute(() -> run(runnable, context));
    }

    /**
     * Execution method of Child Thread delegate
     *
     * @param runnable {@link Runnable}
     * @param context Parent thread MDC content
     */
    private void run(Runnable runnable, Map<String, String> context) {
        // Pass the parent thread's MDC content to the child thread
        if (context != null) {
            try {
                MDC.setContextMap(context);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        try {
            // Perform asynchronous operations
            runnable.run();
        } finally {
            // Empty MDC content
            MDC.clear();
        }
    }
}
