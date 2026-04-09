package ru.university.textbot.quiz;

import ru.university.textbot.quiz.ElementDataLoader.ElementData;

public class UserSession {

    private final long        userId;
    private final ElementData elementData; // данные для создания новых игр

    private ElementGame currentGame;

    private int totalAnswered;
    private int totalCorrect;
    private int currentStreak; // текущая серия правильных ответов
    private int bestStreak;    // лучшая серия за всё время

    public UserSession(long userId, ElementData elementData) {
        this.userId      = userId;
        this.elementData = elementData;
        this.currentGame = null;
    }

    public void startNewGame() {
        currentGame = new ElementGame(elementData);
    }

    public boolean hasActiveGame() {
        return currentGame != null && !currentGame.isFinished();
    }

    public boolean isWaitingForAnswer() {
        return currentGame != null && currentGame.isWaitingForAnswer();
    }

    public ElementGame getCurrentGame() { return currentGame; }

    public void recordAnswer(boolean correct) {
        totalAnswered++;
        if (correct) {
            totalCorrect++;
            currentStreak++;
            if (currentStreak > bestStreak) bestStreak = currentStreak;
        } else {
            currentStreak = 0;
        }
    }

    public long   getUserId()        { return userId; }
    public int    getTotalAnswered()  { return totalAnswered; }
    public int    getTotalCorrect()   { return totalCorrect; }
    public int    getTotalWrong()     { return totalAnswered - totalCorrect; }
    public int    getCurrentStreak()  { return currentStreak; }
    public int    getBestStreak()     { return bestStreak; }

    public double getAccuracy() {
        if (totalAnswered == 0) return 0.0;
        return (double) totalCorrect / totalAnswered * 100.0;
    }

    public String getStatsMessage() {
        if (totalAnswered == 0) {
            return "📊 У тебя пока нет статистики.\nНапиши /quiz чтобы начать!";
        }
        return String.format(
                "📊 Твоя статистика:\n\n"
                        + "✅ Правильных:    %d\n"
                        + "❌ Неправильных:  %d\n"
                        + "📝 Всего:         %d\n"
                        + "🎯 Точность:      %.1f%%\n"
                        + "🔥 Серия сейчас:  %d\n"
                        + "💪 Лучшая серия:  %d",
                totalCorrect,
                getTotalWrong(),
                totalAnswered,
                getAccuracy(),
                currentStreak,
                bestStreak
        );
    }
}