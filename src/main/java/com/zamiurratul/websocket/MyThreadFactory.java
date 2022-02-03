package com.zamiurratul.websocket;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {
    private int threadNumber = 0;

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "cust-thread-" + threadNumber++);
    }
}
