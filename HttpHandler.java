import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

/**
 * HttpHandler
 */
class HttpHandler {
  private String httpMethod;
  private String httpPath;
  static final String CRLF = "\r\n";

  public HttpHandler() {
  }

  /**
   * Fill HTTP header and content properly
   * 
   * @return true if read was successful
   */
  public Boolean nextRequest(BufferedReader bufReader) {
    String request;
    try {

      while ((request = bufReader.readLine()) != null) {
        if (request.isEmpty()) // end of request (CRLF)
          break;

        String[] words = request.split(" ");

        if (words[0].equals("GET")) {
          httpMethod = words[0];
          httpPath = words[1];
        }
      }

      return true;
    } catch (SocketTimeoutException e) {
      System.err.println("Client inactive for too long, deconnexion");
      return false;
    } catch (IOException e) {
      System.err.println("Error reading bufReader" + e.getMessage());
      return false;
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

  public void sendWebPage(OutputStream outStream, String filePath, String responseStatusCode, int cookie) {
    int chunkSize = 128;
    char[] buffer = new char[chunkSize];

    StringBuilder header = new StringBuilder();
    header.append("HTTP/1.1 " + responseStatusCode + CRLF +
        "Content-Type: text/html; charset=UTF-8" + CRLF +
        "Transfer-Encoding: chunked" + CRLF);

    if (cookie != -1)
      header.append("Set-Cookie: SESSID=" + cookie + "; path=/" + CRLF);

    header.append(CRLF);

    try (FileReader file = new FileReader(filePath);) {
      int i;
      String block;

      outStream.write(header.toString().getBytes());
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
