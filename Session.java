
class Session {
  private int sessionId;
  private Boolean sessionClosed = false; // TODO two step sessio closing to warn the user and then close resources
  // manage cookie expiration

  public Session(int sessionId) {
    this.sessionId = sessionId;
  }
}
