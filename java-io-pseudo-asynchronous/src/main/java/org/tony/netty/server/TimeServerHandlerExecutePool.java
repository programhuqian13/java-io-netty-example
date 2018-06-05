package org.tony.netty.server;

import java.util.concurrent.*;

/**
 * @Description 封装task
 *                  时间服务器处理类的线程池，当接受到新的客户端连接的时候，将请求的socket封装成一个Task 然后调用线程池的execute方法
 *                  从而避免了每一个请求接入都要创建一个新的线程
 * @Version 1.0
 * @Author tony
 * @Date 2018/4/26
 * @ProjectName java-io-netty-example
 * @PackageName org.tony.netty.server
 */
public class TimeServerHandlerExecutePool {

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(Integer maxPoolSize,Integer queueSize){
        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,
                120L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task){
        executor.execute(task);
    }

}
