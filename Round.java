import java.util.ArrayList;
import java.util.List;

enum RoundOutcome {
    BLACKJACK,
    WIN,
    PUSH,
    LOSE;

    long settle(int bet) {
        return switch (this) {
            case BLACKJACK -> bet + (3L * bet) / 2;
            case WIN -> bet * 2L;
            case PUSH -> bet;
            case LOSE -> 0;
        };
    }
}

class Round {

    private DeckManager deck;
    private int bet;
    private List<String> playerCard = new ArrayList<>();
    private List<String> dealerCard = new ArrayList<>();
    private int playerTotal;
    private int dealerTotal;
    private boolean dealerCardVisible = true;

    Round(DeckManager deck, int bet) {
        this.deck = deck;
        this.bet = bet;
    }

    int getBet() {
        return bet;
    }

    boolean getDealerCardVisible() {
        return dealerCardVisible;
    }

    List<String> getPlayerCard() {
        return playerCard;
    }

    List<String> getDealerCard() {
        return dealerCard;
    }

    int getPlayerTotal() {
        return playerTotal;
    }

    int getDealerTotal() {
        return dealerTotal;
    }

    RoundOutcome start() {
        playerTotal = deck.pickCard(playerCard, 2);
        dealerTotal = deck.pickCard(dealerCard, 2);

        if (playerTotal == 21 && dealerTotal == 21) {
            return RoundOutcome.PUSH;
        } else if (playerTotal == 21) {
            return RoundOutcome.BLACKJACK;
        } else if (dealerTotal == 21) return RoundOutcome.LOSE;

        dealerCardVisible = false;
        return null;
    }

    RoundOutcome hit() {
        playerTotal = deck.pickCard(playerCard, 1);

        if (playerTotal > 21) {
            dealerCardVisible = true;
            return RoundOutcome.LOSE;
        }
        if (playerTotal == 21 || deck.getDeckIndex() < 0) {
            return stand();
        }
        return null;
    }

    RoundOutcome stand() {
        dealerCardVisible = true;
        dealerPlay();

        if (playerTotal == dealerTotal) return RoundOutcome.PUSH;
        else if (
            playerTotal > dealerTotal || dealerTotal > 21
        ) return RoundOutcome.WIN;
        else return RoundOutcome.LOSE;
    }

    private void dealerPlay() {
        while (dealerTotal <= 16 && deck.getDeckIndex() >= 0)
            dealerTotal = deck.pickCard(dealerCard, 1);
    }

}
