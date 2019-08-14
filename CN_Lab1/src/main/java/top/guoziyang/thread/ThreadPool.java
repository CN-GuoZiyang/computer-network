package top.guoziyang.thread;

import java.util.concurrent.*;

public class ThreadPool {

    private ThreadPool(){super();}

    private final static Integer MINIMUMPOOLSIZE = 3;
    private final static Integer MAXIMUMPOOLSIZE = 10;
    private final static Integer KEEPALIVETIME = 2 * 60;
    private static BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(MINIMUMPOOLSIZE, MAXIMUMPOOLSIZE,
            KEEPALIVETIME, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.AbortPolicy());

    public static Future<?> submit(Callable<?> task) {
        return threadPool.submit(task);
    }

    public static void execute(Runnable task) {
        threadPool.execute(task);
    }

    public static int getSize() {
        return threadPool.getPoolSize();
    }

    public static int getActiveCount() {
        return threadPool.getActiveCount();
    }

}
