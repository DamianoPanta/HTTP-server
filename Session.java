import java.util.Arrays;
import java.util.Random;

class Session {
  private int sessionId; // cookie
  private Boolean sessionClosed = false; // TODO two step sessio closing to warn the user and then close resources
  private DeckManager deckManager;
  private Round currentRound;
  // manage cookie expiration

  public Session(int sessionId) {
    this.sessionId = sessionId;
    this.deckManager = new DeckManager(createDeck());
  }

  private String[] createDeck() {
    // create card deck
    char[] card_rank = {
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'T',
        'J',
        'Q',
        'K',
        'A',
    };
    char[] card_suit = { 'S', 'H', 'D', 'C' };
    String[] deck = new String[52];
    Short index = 0;

    for (char rank : card_rank) {
      for (char suit : card_suit) {
        deck[index] = new StringBuilder()
            .append(rank)
            .append(suit)
            .toString();
        index++;
      }
    }
    return shuffleDeck(deck);
  }

  // Durstenfeld algorithm
  private String[] shuffleDeck(String[] deck) {
    int index;
    String temp;
    Random random = new Random();
    String[] player_deck = Arrays.copyOf(deck, deck.length); // need new object

    for (int i = player_deck.length - 1; i > 0; i--) {
      index = random.nextInt(i + 1); // nextInt(int bound) with bound exclusive, 0<=index<=i
      if (index != i) {
        temp = player_deck[i];
        player_deck[i] = player_deck[index];
        player_deck[index] = temp;
      }
    }
    return player_deck;
  }
}
