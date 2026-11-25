import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BlackjackServer
 */
public class BlackjackServer {
  private static final int PORT = 8000;

  public static void main(String[] args) { // TODO change with group number

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

      while (true) {
        Socket socket = server.accept();
        try {
          socket.setSoTimeout(300000); // 5 minutes timeout TODO SocketTimeoutException when read from it
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

/**
 * Worker
 */
class Worker implements Runnable {
  private HttpHandler httpHandler;
  private Socket socket;
  private InputStream inStream;
  private OutputStream outStream;

  public Worker(Socket socket) {
    this.socket = socket;
    httpHandler = new HttpHandler(this.socket);
    try {
      inStream = this.socket.getInputStream();
      outStream = this.socket.getOutputStream();
    } catch (Exception e) {
      System.err.println("Error creating socket streams" + e.getMessage());
    }
  }

  public void run() {
    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream));
    httpHandler.nextRequest(bufReader);
    if (httpHandler.getMethod().equals("GET")) {
      if (httpHandler.getPath().equals("/")) {
        httpHandler.redirection(outStream, "http://localhost:8000/play.html");

      } else if (httpHandler.getPath().equals("/play.html")) {
        httpHandler.sendWebPage(outStream, "./play.html", "200 OK");
      } else {
        httpHandler.sendWebPage(outStream, "./notFound.html", "404 Not Found");
      }
    }
    System.out.println("\nWorker finished");
    try {
      socket.close();
    } catch (IOException e) {
      System.err.println("Error closing a worker socket" + e.getMessage());
    }
  }
}

/**
 * HttpHandler
 */
class HttpHandler {
  private String httpMethod;
  private String httpPath;
  final private String CRLF = "\r\n";

  public HttpHandler(Socket socket) {
  }

  /**
   * Fill HTTP header and content properly
   */
  public void nextRequest(BufferedReader bufReader) {
    try {
      String request = bufReader.readLine();
      String[] words = request.split(" ");

      httpMethod = words[0];
      httpPath = words[1];

    } catch (IOException e) {
      System.err.println("Error reading bufReader" + e.getMessage());
    }
  }

  public String getMethod() {
    return httpMethod;
  }

  public String getPath() {
    return httpPath;
  }

  public void redirection(OutputStream outStream, String location) {
    String header = "HTTP/1.1 303 See Other" + CRLF +
        "Location: " + location + CRLF +
        CRLF;

    try {
      outStream.write(header.getBytes());
      outStream.flush();
    } catch (IOException e) {
      System.err.println("Error sending redirection" + e.getMessage());
    }
  }

  public void sendWebPage(OutputStream outStream, String filePath, String responseStatusCode) {
    int chunkSize = 128;
    char[] buffer = new char[chunkSize];

    String header = "HTTP/1.1 " + responseStatusCode + CRLF +
        "Content-Type: text/html; charset=UTF-8" + CRLF +
        "Transfer-Encoding: chunked" + CRLF +
        CRLF;

    try (FileReader file = new FileReader(filePath);) {
      int i;
      String block;

      outStream.write(header.getBytes());
      outStream.flush();

      while ((i = file.read(buffer)) != -1) { // send by chunked encoding
        // recompute real size in case of special characters
        byte[] data = new String(buffer, 0, i).getBytes();
        block = Integer.toHexString(data.length) + CRLF + new String(buffer, 0, i) + CRLF;
        outStream.write(block.getBytes());
        outStream.flush();
      }

      block = 0 + CRLF + CRLF;
      outStream.write(block.getBytes());
      outStream.flush();

    } catch (FileNotFoundException e) {
      System.err.println("File play.html not found" + e.getMessage());
    } catch (IOException e) {
      System.err.println("Error reading from play.html file" + e.getMessage());
      e.printStackTrace();
    }

  }
}
