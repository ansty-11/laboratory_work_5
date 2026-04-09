package ru.university.textbot.model;

public class Element {

    private final String symbol;  // "Fe", "Au", "Na" — уникален
    private final String name;    // "Железо", "Золото", "Натрий"

    public Element(String symbol, String name) {
        this.symbol = symbol;
        this.name   = name;
    }

    public String getSymbol() { return symbol; }
    public String getName()   { return name; }

    @Override
    public String toString() {
        return symbol + " — " + name;
    }
}