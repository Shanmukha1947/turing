
import java.util.concurrent.locks.StampedLock;

public class ThreadSafeCounter {

    private long count = 0;
    private final StampedLock lock = new StampedLock();

    public void increment() {
        long stamp = lock.writeLock();
        try {
            count++;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public long getCount() {
        long stamp = lock.tryOptimisticRead();
        long currentCount = count;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentCount = count;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return currentCount;
    }

    public static void main(String[] args) {
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
