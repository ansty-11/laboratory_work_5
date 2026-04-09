package ru.university.textbot.processor;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.university.textbot.model.Element;
import ru.university.textbot.model.ElementQuestion;
import ru.university.textbot.quiz.ElementDataLoader;
import ru.university.textbot.quiz.ElementDataLoader.ElementData;
import ru.university.textbot.quiz.ElementGame;
import ru.university.textbot.quiz.SessionManager;
import ru.university.textbot.quiz.UserSession;

import static ru.university.textbot.config.BotConfig.BOT_TOKEN;

public class TextProcessor implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final SessionManager sessionManager;

    public TextProcessor() {
        telegramClient = new OkHttpTelegramClient(BOT_TOKEN);

        ElementData data = ElementDataLoader.loadFromResources("elements.csv");
        sessionManager = new SessionManager(data);
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text   = update.getMessage().getText().trim();
        long   chatId = update.getMessage().getChatId();
        long   userId = update.getMessage().getFrom().getId();

        String response = handleMessage(userId, text);
        sendText(chatId, response);
    }


    private String handleMessage(long userId, String text) {

        switch (text) {

            case "/start":
                return "⚗️ Привет! Я бот-викторина по химическим элементам!\n\n"
                        + "Команды:\n"
                        + "/quiz           — начать викторину\n"
                        + "/stats          — твоя статистика\n"
                        + "/element <Fe>   — справка по символу\n"
                        + "/reset          — сбросить сессию\n"
                        + "/help           — помощь";

            case "/help":
                return "📖 Как играть:\n\n"
                        + "1. Напиши /quiz\n"
                        + "2. Я покажу символ элемента, например: Au\n"
                        + "3. Ответь его названием: Золото\n\n"
                        + "Пример:\n"
                        + "Бот: ⚗️ Как называется элемент с символом Au?\n"
                        + "Ты:  Золото ✅";

            case "/stats": {
                UserSession session = sessionManager.get(userId);
                if (session == null) {
                    return "📊 У тебя пока нет статистики.\nНапиши /quiz чтобы начать!";
                }
                return session.getStatsMessage();
            }

            case "/reset": {
                sessionManager.remove(userId);
                return "🔄 Сессия сброшена. Напиши /quiz чтобы начать заново!";
            }

            case "/quiz":
                return handleQuizCommand(userId);
        }

        if (text.startsWith("/element")) {
            return handleElementLookup(userId, text);
        }

        return handleAnswer(userId, text);
    }


    private String handleQuizCommand(long userId) {
        UserSession session = sessionManager.getOrCreate(userId);

        if (session.isWaitingForAnswer()) {
            return "⏳ Ответь на текущий вопрос:\n\n"
                    + session.getCurrentGame().getCurrentQuestion().getQuestion();
        }

        if (!session.hasActiveGame()) {
            session.startNewGame();
        }

        return askNextQuestion(session);
    }


    private String handleAnswer(long userId, String text) {
        UserSession session = sessionManager.get(userId);

        if (session == null || !session.isWaitingForAnswer()) {
            return "Напиши /quiz чтобы начать викторину, или /help для справки.";
        }

        ElementGame game = session.getCurrentGame();
        boolean correct;

        try {
            correct = game.checkAnswer(text);
        } catch (IllegalStateException e) {
            return "⚠ Что-то пошло не так. Напиши /quiz чтобы начать.";
        }

        session.recordAnswer(correct);

        StringBuilder sb = new StringBuilder();

        if (correct) {
            sb.append("✅ Правильно!\n");
        } else {
            sb.append("❌ Неверно.\n")
                    .append("Правильный ответ: ").append(game.getCurrentCorrectAnswer()).append("\n");
        }

        sb.append(String.format("📈 Правильных: %d / %d\n",
                session.getTotalCorrect(), session.getTotalAnswered()));

        if (game.getRemainingCount() > 0) {
            sb.append("\n").append(askNextQuestion(session));
        } else {
            sb.append("\n🏁 Викторина завершена!\n\n")
                    .append(session.getStatsMessage())
                    .append("\n\nНапиши /quiz для новой игры!");
        }

        return sb.toString();
    }


    private String askNextQuestion(UserSession session) {
        ElementGame     game     = session.getCurrentGame();
        ElementQuestion question = game.nextQuestion();

        if (question == null) {
            return "🏁 Все элементы пройдены!\n\n"
                    + session.getStatsMessage()
                    + "\n\nНапиши /quiz для новой игры!";
        }

        return String.format(
                "❓ Вопрос %d / %d\n\n%s",
                game.getAskedCount(),
                game.getTotalCount(),
                question.getQuestion()
        );
    }

    private String handleElementLookup(long userId, String text) {
        String symbol = text.replaceFirst("^/element\\s*", "").trim();

        if (symbol.isEmpty()) {
            return "📖 Использование: /element <символ>\nПример: /element Fe";
        }

        String normalized = symbol.length() == 1
                ? symbol.toUpperCase()
                : symbol.substring(0, 1).toUpperCase() + symbol.substring(1).toLowerCase();

        UserSession session = sessionManager.getOrCreate(userId);
        if (session.getCurrentGame() == null) {
            session.startNewGame();
        }

        Element found = session.getCurrentGame().findBySymbol(normalized);

        if (found == null) {
            return "🔍 Элемент с символом «" + normalized + "» не найден.\nПроверь написание.";
        }

        return "🧪 " + found.getSymbol() + " — " + found.getName();
    }


    private void sendText(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка отправки: " + e.getMessage());
        }
    }
}