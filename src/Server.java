import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
public class Server {
    public static void main(String[] args) throws Exception {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel
                .open();// www  .  ja va2 s  .c om
        String host = "localhost";
        int port = 8989;
        InetSocketAddress sAddr = new InetSocketAddress(host, port);
        server.bind(sAddr);
        System.out.format("Server is listening at %s%n", sAddr);
        AttachmentServer attach = new AttachmentServer();
        attach.server = server;
        server.accept(attach, new ConnectionHandlerServer());
        Thread.currentThread().join();
    }
}
class AttachmentServer {
    AsynchronousServerSocketChannel server;
    AsynchronousSocketChannel client;
    ByteBuffer buffer;
    SocketAddress clientAddr;
    boolean isRead;
}

class ConnectionHandlerServer implements
        CompletionHandler<AsynchronousSocketChannel, AttachmentServer> {
    @Override
    public void completed(AsynchronousSocketChannel client, AttachmentServer attach) {
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            System.out.format("Accepted a  connection from  %s%n", clientAddr);
            attach.server.accept(attach, this);
            ReadWriteHandlerServer rwHandler = new ReadWriteHandlerServer();
            AttachmentServer newAttach = new AttachmentServer();
            newAttach.server = attach.server;
            newAttach.client = client;
            newAttach.buffer = ByteBuffer.allocate(2048);
            newAttach.isRead = true;
            newAttach.clientAddr = clientAddr;
            client.read(newAttach.buffer, newAttach, rwHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, AttachmentServer attach) {
        System.out.println("Failed to accept a  connection.");
        e.printStackTrace();
    }
}

class ReadWriteHandlerServer implements CompletionHandler<Integer, AttachmentServer> {
    @Override
    public void completed(Integer result, AttachmentServer attach) {
        if (result == -1) {
            try {
                attach.client.close();
                System.out.format("Stopped   listening to the   client %s%n",
                        attach.clientAddr);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (attach.isRead) {
            attach.buffer.flip();
            int limits = attach.buffer.limit();
            byte bytes[] = new byte[limits];
            attach.buffer.get(bytes, 0, limits);
            Charset cs = Charset.forName("UTF-8");
            String msg = new String(bytes, cs);
            System.out.format("Client at  %s  says: %s%n", attach.clientAddr,
                    msg);
            attach.isRead = false; // It is a write
            attach.buffer.rewind();

        } else {
            // Write to the client
            attach.client.write(attach.buffer, attach, this);
            attach.isRead = true;
            attach.buffer.clear();
            attach.client.read(attach.buffer, attach, this);
        }
    }

    @Override
    public void failed(Throwable e, AttachmentServer attach) {
        e.printStackTrace();
    }
}