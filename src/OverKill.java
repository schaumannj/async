import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by schaumanni on 10/8/15.
 */
public class OverKill {

    private static final Logger log = Logger.getLogger(OverKill.class.getName());

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        int threadNo = 100;
        int loop = 10000;
        List<String> list = Arrays.asList(new String[]{"honza", "Tomas"});
        log.info("Performance test will Begin  looping {} for {} threads and commands {}"); // , loop, threadNo, list

        ExecutorService threadPool = Executors.newFixedThreadPool(threadNo);

        for(String command : list) {
            for (int i = 0; i < loop; i++) {
                threadPool.submit(new Commander(command + i));
            }
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            Thread.sleep(10L);
        }
        log.info("The test finish and took " + (System.currentTimeMillis() - start) + " ms");



    }

}
