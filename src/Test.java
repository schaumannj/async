import java.util.Date;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Created by schaumanni on 10/6/15.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            IntStream.range(0, 10).forEach(
                    nbr -> {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(new Date(System.currentTimeMillis()) + " Jede " + threadName);
                        try {
                            TimeUnit.MILLISECONDS.sleep((long)(Math.random() * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


            );
        });


        executor.submit(() -> {
            IntStream.range(0, 10).forEach(
                    nbr -> {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(new Date(System.currentTimeMillis()) + " VALIII " + threadName);
                        try {
                            TimeUnit.MILLISECONDS.sleep((long)(Math.random() * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


            );
        });

    }
}
