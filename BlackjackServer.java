import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.Authenticator.RequestorType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BlackjackServer
 */
public class BlackjackServer {
  private static final int PORT = 8000;

  public static void main(String[] args) { // TODO change with group number
    try (ServerSocket server = new ServerSocket(PORT);) {
      ExecutorService threadPool = Executors.newFixedThreadPool(3); // try-with-resource only from JDK 19

      try {
        while (true) {
          Socket client = server.accept();
          client.setSoTimeout(300000); // 5 minutes timeout TODO SocketTimeoutException when read from it
          Worker worker = new Worker(client);
          threadPool.execute(worker);
        }
      } finally {
        threadPool.shutdown();
        try {
          if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
          }
        } catch (InterruptedException e) {
          System.err.println("Threads interrupted during shutdown " + e.getMessage());
        }
      }

    } catch (IOException e) {
      System.err.println("Network error: " + e.getMessage());
    }
  }
}

/**
 * Worker
 */
class Worker implements Runnable {
  // private final String name;
  private final Socket client;
  private InputStream inStream;
  private OutputStream outStream;

  public Worker(Socket client) {
    this.client = client;
    try {
      this.inStream = client.getInputStream();
      this.outStream = client.getOutputStream();
    } catch (IOException e) {
      System.err.println("Error getting streams from client socket" + e.getMessage());
    }
  }

  public void run() {
    byte[] buffer = new byte[32];

    while (true) {
      try {
        int len = inStream.read(buffer);
        if (len <= 0) break;
      } catch (IOException e) {
        System.err.println("Error reading from client socket" + e.getMessage());
      }
      String request = new String(buffer);
      System.out.print(request);
    }

    System.out.println("\nWorker finished");
  }
}
