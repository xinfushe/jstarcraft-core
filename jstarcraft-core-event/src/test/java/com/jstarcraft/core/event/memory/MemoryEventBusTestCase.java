package com.jstarcraft.core.event.memory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.EventBusTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.utility.NameThreadFactory;

public class MemoryEventBusTestCase extends EventBusTestCase {

    protected static final Logger logger = LoggerFactory.getLogger(MemoryEventBusTestCase.class);

    private ThreadPoolExecutor pool;

    

    @Before
    public void start() {
        NameThreadFactory factory = new NameThreadFactory("EventBus");
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(1000);
        pool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, queue, factory);
        logger.info("事件总线已启动");
    }

    @After
    public void stop() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1000, TimeUnit.SECONDS)) {
                logger.error("事件总线无法在等待时间内完成,可能存在部分事件丢失");
                pool.shutdownNow();
            }
        } catch (InterruptedException exception) {
            logger.error("事件总线由于在等待时间内中断,可能存在部分事件丢失");
            pool.shutdownNow();
        }
        logger.info("事件总已停止");
    }

    
    @Override
    protected EventBus getEventBus(EventMode mode) {
        switch (mode) {
        case QUEUE: {
            return new MemoryQueueEventBus(1000);
        }
        case TOPIC: {
            return new MemoryTopicEventBus(pool);
        }
        default: {
            return null;
        }
        }
    }
}
