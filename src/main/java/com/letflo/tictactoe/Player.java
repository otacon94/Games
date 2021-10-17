package com.letflo.tictactoe;

public class Player {
    public enum Type {X, Y}

    Type type;

    public Player(Type type) {
        this.type = type;
    }

    public Type getType() { return type; }

    public void setType(Type type) { this.type = type; }
}
