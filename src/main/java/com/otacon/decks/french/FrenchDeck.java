package com.otacon.decks.french;

import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import static com.otacon.decks.french.FrenchCard.Type.*;

public class FrenchDeck {


    public LinkedList<FrenchCard> cards;

    public FrenchDeck() {
        cards = new LinkedList<>();
        initializeDeck();
        shuffleDeck();
    }

    public void initializeDeck() {
        cards.clear();
        addType(DIAMONDS);
        addType(CLUBS);
        addType(SPADES);
        addType(HEARTS);
        addJokers();
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public FrenchCard pull() throws NoSuchElementException {
        return cards.removeFirst();
    }

    public FrenchCard pick() {
        return cards.peek();
    }

    private void addType(FrenchCard.Type t) {
        for (int i = 1; i <= 13; i++) {
            cards.add(new FrenchCard(t, FrenchCard.Value.getByNumericValue(i)));
        }
    }

    private void addJokers() {
        cards.add(new FrenchCard(JOKER_RED, FrenchCard.Value.JOKER));
        cards.add(new FrenchCard(JOKER_BLACK, FrenchCard.Value.JOKER));
    }

    @Override
    public String toString() {
        return "FrenchDeck{" +
                "cards=" + cards + "\n" +
                "size=" + cards.size() + "\n" +
                '}';
    }
}
