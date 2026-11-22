import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BlackjackServer
 */
public class BlackjackServer {
  public static void main(String[] args) {
    try (
        ServerSocket server = new ServerSocket(8000); // TODO change with group number
    ) {
      ExecutorService threadPool = Executors.newFixedThreadPool(3); // try-with-resource only from JDK 19
      while (true) {
        Socket client = server.accept();
        Worker worker = new Worker(client);
        threadPool.execute(worker);
      }
      
      threadPool.shutdown();    // TODO release correctly the threadPool
      try {
        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
          threadPool.shutdownNow();
        }
      } catch (InterruptedException e) {
        System.err.println("Threads interrupted during shutdown " + e.getMessage());
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

  public Worker(Socket client) {
    this.client = client;
  }

  public void run() {
    System.out.println("Hi from running client");
  }
}
