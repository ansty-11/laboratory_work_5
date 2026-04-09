package ru.university.textbot.quiz;

import ru.university.textbot.model.Element;
import ru.university.textbot.model.ElementQuestion;
import ru.university.textbot.quiz.ElementDataLoader.ElementData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ElementGame {

    private final List<Element>         allElements;  // все элементы
    private final Map<String, Element>  bySymbol;     // HashMap: символ → элемент
    private final Set<String>           askedSymbols; // HashSet: уже заданные символы

    private ElementQuestion currentQuestion;
    private boolean         waitingForAnswer;
    private final Random    random = new Random();

    public ElementGame(ElementData data) {
        this.allElements   = data.elements;
        this.bySymbol      = data.bySymbol;
        this.askedSymbols  = new HashSet<>();
        this.currentQuestion  = null;
        this.waitingForAnswer = false;
    }

    public ElementQuestion nextQuestion() {
        List<Element> remaining = new ArrayList<>();
        for (Element e : allElements) {
            if (!askedSymbols.contains(e.getSymbol())) { // HashSet.contains — O(1)
                remaining.add(e);
            }
        }

        if (remaining.isEmpty()) {
            currentQuestion  = null;
            waitingForAnswer = false;
            return null;
        }

        Element chosen = remaining.get(random.nextInt(remaining.size()));
        askedSymbols.add(chosen.getSymbol()); // HashSet.add — O(1)

        currentQuestion  = new ElementQuestion(chosen);
        waitingForAnswer = true;
        return currentQuestion;
    }

    public boolean checkAnswer(String userAnswer) {
        if (currentQuestion == null) {
            throw new IllegalStateException("Нет активного вопроса");
        }
        waitingForAnswer = false;
        return currentQuestion.isCorrect(userAnswer);
    }

    public Element findBySymbol(String symbol) {
        return bySymbol.get(symbol); // O(1)
    }

    public String          getCurrentCorrectAnswer() { return currentQuestion == null ? "" : currentQuestion.getCorrectAnswer(); }
    public ElementQuestion getCurrentQuestion()      { return currentQuestion; }
    public boolean         isWaitingForAnswer()      { return waitingForAnswer; }
    public int             getAskedCount()           { return askedSymbols.size(); }
    public int             getTotalCount()           { return allElements.size(); }
    public int             getRemainingCount()       { return allElements.size() - askedSymbols.size(); }
    public boolean         isFinished()              { return getRemainingCount() == 0 && !waitingForAnswer; }
}