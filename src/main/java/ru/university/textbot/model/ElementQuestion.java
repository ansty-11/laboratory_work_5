package ru.university.textbot.model;

public class ElementQuestion {

    private final Element element;

    public ElementQuestion(Element element) {
        this.element = element;
    }

    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null || userAnswer.isBlank()) return false;
        return userAnswer.trim().equalsIgnoreCase(element.getName().trim());
    }

    public String getQuestion() {
        return "⚗️ Как называется элемент с символом *" + element.getSymbol() + "*?";
    }

    public String getCorrectAnswer() {
        return element.getName();
    }

    public Element getElement() { return element; }
}