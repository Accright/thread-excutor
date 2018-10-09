import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 自定义JAVA简单线程池
 * AccRight
 * v0.1
 * 使用BlockingQueue阻塞队列作为任务队列 不断创建线程取task
 * 线程池简单实现
 */
public class CuzExcutor {
    public volatile boolean RUNNING = true;//是否正在运行
    //任务的阻塞队列
    private static BlockingQueue<Runnable> taskQueue = null;
    //具体执行任务的worker
    private Set<Worker> workerSet = new HashSet<Worker>();
    //开启的线程列表 worker的执行需要thread
    private List<Thread> threadList = new ArrayList<Thread>();

    //最大线程数
    int poolSize = 0;
    //当前线程数
    int coreSize = 0;
    //worker是否停止
    boolean shutdown = false;

    //构造函数 传入线程数
    public CuzExcutor(int poolSize){
        this.poolSize = poolSize;
        this.coreSize = 0;
        taskQueue = new LinkedBlockingQueue<Runnable>(poolSize);//任务列表大小  超出线程池大小阻塞
    }

    //线程池执行方法
    public void excute(Runnable runnable) throws InterruptedException {
        if(runnable == null) {
            throw new NullPointerException();//抛出空指针异常
        }
        //如果当前线程未达到线程池大小 添加线程执行
        if(coreSize < poolSize){
            addThread(runnable);
        }else{
            taskQueue.put(runnable);
        }
    }

    //添加线程执行start
    public void addThread(Runnable runnable){
        //当前线程数+1
        coreSize ++;
        Worker worker = new Worker(runnable);//创建新的Worker 同时任务队列取出该任务
        workerSet.add(worker);
        //开启新线程执行worker
        Thread thread = new Thread(worker);
        threadList.add(thread);//线程列表+1
        //执行thread
        thread.start();
    }

    //停止所有线程
    public void shutdown(){
        RUNNING = false;//停止获取任务Queue
        if(! workerSet.isEmpty()){
            for (Worker worker : workerSet){
                worker.interruptIfIdle();
            }
        }
        shutdown = true;
        Thread.currentThread().interrupt();
    }

    //执行的内部类
    private class Worker implements Runnable{

        //构造函数 worker获得数据 从queue中删除数据
        public Worker(Runnable runnable){
            taskQueue.offer(runnable);
        }

        @Override
        public void run() {
            while(RUNNING){
                if(shutdown == true){
                    Thread.interrupted();//关掉当前线程
                }
                Runnable workRunable = null;
                try {
                    workRunable = getTask();
                    workRunable.run();//执行自定义的run方法
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //取任务
        public Runnable getTask() throws InterruptedException {
            return taskQueue.take();
        }

        //停止任务的方法
        public void interruptIfIdle(){
            for (Thread thread : threadList){
                System.out.println(">>>>>>正在停止线程"+thread.getName());
                thread.interrupt();
            }
        }
    }
}
