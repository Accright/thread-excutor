public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("自定义线程池执行测试>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        CuzExcutor cuzExcutor = new CuzExcutor(10);//创建为2 的线程池
        for (int i = 0;i < 100;i++){
            cuzExcutor.excute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(">>>>>>>>线程"+Thread.currentThread().getName()+"正在执行");
                }
            });
        }
        cuzExcutor.shutdown();
    }
}
