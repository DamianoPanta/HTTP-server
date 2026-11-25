import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.InputStreamReader;

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
    httpHandler = new HttpHandler();
    try {
      inStream = this.socket.getInputStream();
      outStream = this.socket.getOutputStream();
    } catch (Exception e) {
      System.err.println("Error creating socket streams" + e.getMessage());
    }
  }

  public void run() {
    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream));

    if (httpHandler.nextRequest(bufReader)) {

      if (httpHandler.getMethod().equals("GET")) {
        if (httpHandler.getPath().equals("/")) {
          httpHandler.redirection(outStream, "http://localhost:8000/play.html");
        } else if (httpHandler.getPath().equals("/play.html")) {
          httpHandler.sendWebPage(outStream, "./play.html", "200 OK");
        } else {
          httpHandler.sendWebPage(outStream, "./notFound.html", "404 Not Found");
        }
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
