package com.mnzit.spring.redis.redisdemo.support;

import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
public class AwaitThreadContainer {
    private final Map<String, Set<Thread>> threadMap = new ConcurrentHashMap<>();

    /**
     * Thread wait, maximum wait 100 milliseconds
     *
     * @param key Cache Key
     * @throws InterruptedException {@link InterruptedException}
     * @ param milliseconds waiting time
     */
    public final void await(String key, long milliseconds) throws InterruptedException {
        // Test if the current thread has been interrupted
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        Set<Thread> threadSet = threadMap.get(key);
        // Determine if the thread container is null, and if so, create a new one
        if (threadSet == null) {
            threadSet = new ConcurrentSkipListSet<>(Comparator.comparing(Thread::toString));
            threadMap.put(key, threadSet);
        }

        // Put threads into containers
        threadSet.add(Thread.currentThread());

        // Blocking for a certain time
        LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(milliseconds));
    }

    /**
     * Thread wake-up
     *
     * @param key key
     */
    public final void signalAll(String key) {
        Set<Thread> threadSet = threadMap.get(key);

        // Determines whether the waiting thread container corresponding to the key is null
        if (!CollectionUtils.isEmpty(threadSet)) {
            for (Thread thread : threadSet) {
                LockSupport.unpark(thread);
            }

            // Empty the waiting thread container
            threadSet.clear();
        }
    }
}
