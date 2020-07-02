package com.smart.financial.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class SmartTaskExecutor {

    private ExecutorService executorService;

    private int corePoolSize = 1;
    private int maximumPoolSize = 10;
    private long keepAliveTime = 60L;

    public SmartTaskExecutor() {
        executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>());
    }

    public void execute(Runnable command){
        executorService.execute(command);
    }
}
