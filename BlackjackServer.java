import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

/**
 * BlackjackServer
 */
public class BlackjackServer {
  private static final int PORT = 8000; // TODO change with group number

  public static void main(String[] args) {

    if (args.length != 1) {
      System.err.println("Incorrect number of argument");
      return;
    }

    int maxThreads;
    try {
      maxThreads = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.err.println("Please enter a valid number for the number of threads");
      return;
    }
    ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads); // try-with-resource only from JDK 19

    try (ServerSocket server = new ServerSocket(PORT);) {

      System.out.println("Server started ...");

      while (server.isBound() && !server.isClosed()) {
        Socket socket = server.accept();
        try {
          socket.setSoTimeout(300000); // 5 minutes timeout
          threadPool.execute(new Worker(socket));
        } catch (Exception e) {
          System.err.println("Error when creating worker" + e.getMessage());
          socket.close();
        }
      }
    } catch (IOException e) {
      System.err.println("Networking error" + e.getMessage());
    } finally {
      shutdownPool(threadPool);
    }
  }

  private static void shutdownPool(ExecutorService pool) {
    pool.shutdown();
    try {
      if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
        pool.shutdownNow();
      }
    } catch (InterruptedException e) {
      System.err.println("Threads interrupted during shutdown " + e.getMessage());
    }
  }
}
