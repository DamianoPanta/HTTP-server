import java.util.Arrays;
import java.util.List;

class DeckManager {

    private int deckIndex;
    private String[] playerDeck;

    DeckManager(String[] playerDeck) {
        this.playerDeck = playerDeck;
        deckIndex = playerDeck.length - 1;
    }

    int getDeckIndex() {
        return deckIndex;
    }

    int pickCard(List<String> previous_card, int card_amount) {
        deckIndex -= card_amount;
        String[] new_card = Arrays.copyOfRange(
            playerDeck,
            deckIndex + 1,
            deckIndex + card_amount + 1
        );

        for (String card : new_card) {
            previous_card.add(card);
        }

        int total = 0;
        int aces = 0;
        for (String card : previous_card) {
            try {
                String rank = card.split("")[0];
                if (
                    rank.equals("T") ||
                    rank.equals("J") ||
                    rank.equals("Q") ||
                    rank.equals("K")
                ) total += 10;
                else if (rank.equals("A")) {
                    aces++;
                    total += 11;
                } else total += Integer.parseInt(rank);
            } catch (NumberFormatException e) {
                System.err.println(
                    "Error reading card number: " + e.getMessage()
                );
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }
}
