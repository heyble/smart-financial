package com.smart.financial.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class SmartTaskExecutor {

    private ExecutorService executorService;

    private int corePoolSize = 50;
    private int maximumPoolSize = 50;
    private long keepAliveTime = 60L;

    public SmartTaskExecutor() {
        executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public void execute(Runnable command){
        executorService.execute(command);
    }
}
