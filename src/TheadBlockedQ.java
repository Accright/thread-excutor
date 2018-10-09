public class TheadBlockedQ {
    public static void main(String[] args) throws InterruptedException {
        ThreadExcutor excutor = new ThreadExcutor(10);
        for (int i = 0; i < 100; i++) {
            excutor.exec(new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程 " + Thread.currentThread().getName() + " 在帮我干活");
                }
            });
        }
        excutor.shutdown();
    }
}