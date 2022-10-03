package com.otacon.decks.french;

import java.util.Arrays;

public class FrenchCard {

    public enum Type {
        DIAMONDS, HEARTS, SPADES, CLUBS, JOKER_RED, JOKER_BLACK;

        public String unicodeSymbol() {
            switch (this) {
                case SPADES:
                    return "♠";
                case HEARTS:
                    return "♥";
                case CLUBS:
                    return "♣";
                default:
                    return "♦️";
            }
        }
    }

    public enum Value {
        ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        KNAVE(11), KNIGHT(12), KING(13), JOKER(-1);

        int numericValue;

        Value(int value) {
            this.numericValue = value;
        }

        public static FrenchCard.Value getByNumericValue(int value) {
            return Arrays
                    .stream(com.otacon.decks.french.FrenchCard.Value.values())
                    .filter(c -> c.numericValue == value)
                    .findFirst()
                    .orElse(com.otacon.decks.french.FrenchCard.Value.ACE);
        }

        public String unicodePrefix() {
            switch (this) {
                case KNAVE:
                    return "J";
                case KNIGHT:
                    return "Q";
                case KING:
                    return "K";
                case JOKER:
                    return "JOKER";
                default:
                    return this.numericValue + "";
            }
        }

        public int getNumericValue() { return numericValue; }
    }

    private FrenchCard.Type type;
    private FrenchCard.Value value;

    public FrenchCard(FrenchCard.Type type, FrenchCard.Value value) {
        this.type = type;
        this.value = value;
    }

    public FrenchCard.Type getType() { return type; }

    public void setType(FrenchCard.Type type) { this.type = type; }

    public FrenchCard.Value getValue() { return value; }

    public void setValue(FrenchCard.Value value) { this.value = value; }

    @Override
    public String toString() {
        if (this.value == Value.JOKER) {
            return String.format("(JOKER(%s))",
                    type == Type.JOKER_BLACK ? 'B' : 'R');
        }
        return String.format("(%s%s)",
                type.unicodeSymbol(), value.unicodePrefix());
    }
}

