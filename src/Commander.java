import java.util.logging.Logger;

/**
 * Created by schaumanni on 10/8/15.
 */
public class Commander implements Runnable {
    private static final Logger log = Logger.getLogger(Commander.class.getName());
    private String command;

    public Commander(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        log.info(Thread.currentThread().getName() + "  " + command );
    }
}
