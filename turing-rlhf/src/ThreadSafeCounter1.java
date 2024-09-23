import java.util.concurrent.atomic.AtomicReference;

public class ThreadSafeCounter1 {

    private final AtomicReference<Integer> count = new AtomicReference<>(0);

    public void increment() {
        count.updateAndGet(current -> current + 1);
    }

    public int getCount() {
        return count.get();
    }

    public static void main(String[] args){
        ThreadSafeCounter counter = new ThreadSafeCounter();
        final int NUM_THREADS = 100;
        final int NUM_INCREMENTS = 100000;

        Runnable task = () -> {
            for (int i = 0; i < NUM_INCREMENTS; i++) {
                counter.increment();
            }
        };

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(task);
        }

        long startTime = System.nanoTime();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();

        System.out.println("Final Count: " + counter.getCount());
        System.out.println("Execution Time: " + (double) (endTime - startTime) / 1_000_000 + " ms");
    }
}
