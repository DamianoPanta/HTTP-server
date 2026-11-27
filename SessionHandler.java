import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session
 */
public class SessionHandler {
  static private Random random = new Random();
  static private ConcurrentHashMap<Integer, Session> map = new ConcurrentHashMap<>();
  static private final int maxSession = 1000;

  /**
   * @return -1 if creation of session failed, session id otherwise
   */
  public static int createSession() {
    int id;
    while (true) {
      id = random.nextInt(maxSession);
      if (!map.containsKey(id))
        break;
      else if (map.size() >= maxSession) {
        return -1;
      }
    }
    map.put(id, new Session(id));
    return id;
  }
}
