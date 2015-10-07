import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
/*from  ww w .j  a  va2 s.  co m*/
public class Client {
    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 8989);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Connected");
        AttachmentClient attach = new AttachmentClient();
        attach.channel = channel;
        attach.buffer = ByteBuffer.allocate(2048);
        attach.isRead = false;
        attach.mainThread = Thread.currentThread();

        Charset cs = Charset.forName("UTF-8");
        String msg = "Hello";
        byte[] data = msg.getBytes(cs);
        attach.buffer.put(data);
        attach.buffer.flip();

        ReadWriteHandlerClient readWriteHandler = new ReadWriteHandlerClient();
        channel.write(attach.buffer, attach, readWriteHandler);
        attach.mainThread.join();
    }
}
class AttachmentClient {
    AsynchronousSocketChannel channel;
    ByteBuffer buffer;
    Thread mainThread;
    boolean isRead;
}
class ReadWriteHandlerClient implements CompletionHandler<Integer, AttachmentClient> {
    @Override
    public void completed(Integer result, AttachmentClient attach) {
        if (attach.isRead) {
            attach.buffer.flip();
            Charset cs = Charset.forName("UTF-8");
            int limits = attach.buffer.limit();
            byte bytes[] = new byte[limits];
            attach.buffer.get(bytes, 0, limits);
            String msg = new String(bytes, cs);
            System.out.format("Server Responded: "+ msg);
            try {
                msg = this.getTextFromUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (msg.equalsIgnoreCase("bye")) {
                attach.mainThread.interrupt();
                return;
            }
            attach.buffer.clear();
            byte[] data = msg.getBytes(cs);
            attach.buffer.put(data);
            attach.buffer.flip();
            attach.isRead = false; // It is a write
            attach.channel.write(attach.buffer, attach, this);
        }else {
            attach.isRead = true;
            attach.buffer.clear();
            attach.channel.read(attach.buffer, attach, this);
        }
    }
    @Override
    public void failed(Throwable e, AttachmentClient attach) {
        e.printStackTrace();
    }
    private String getTextFromUser() throws Exception{
        System.out.print("Please enter a  message  (Bye  to quit):");
        BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in));
        String msg = consoleReader.readLine();
        return msg;
    }
}