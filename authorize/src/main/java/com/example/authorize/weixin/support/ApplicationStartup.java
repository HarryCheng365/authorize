package com.example.authorize.weixin.support;

import com.example.authorize.weixin.entity.Token;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ApplicationStartup implements ApplicationListener<ContextStartedEvent> {
    @Override
    public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 60,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });

        TokenManager tokenManager = contextStartedEvent.getApplicationContext().getBean(TokenManager.class);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tokenManager.initAll();
            }
        };
        configDaemonThread(threadPoolExecutor,runnable,"TokenManager").start();


    }

    private Thread configThread(ThreadPoolExecutor threadPoolExecutor,Runnable runnable,String name){
        Thread t=threadPoolExecutor.getThreadFactory().newThread(runnable);
        t.setName(name);
        return t;
    }

    private Thread configDaemonThread(ThreadPoolExecutor threadPoolExecutor,Runnable runnable,String name){
        Thread t=threadPoolExecutor.getThreadFactory().newThread(runnable);
        t.setDaemon(true);
        t.setName(name);
        return t;
    }

}
