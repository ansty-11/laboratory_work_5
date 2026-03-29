package ru.university.textbot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static ru.university.textbot.config.BotConfig.BOT_TOKEN;

public class TextProcessorBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    // Конструктор принимает токен для инициализации клиента
    public TextProcessorBot() {
        telegramClient = new OkHttpTelegramClient(BOT_TOKEN);
    }

    @Override
    public void consume(Update update) {
        // Проверяем, что полученное обновление содержит текстовое сообщение
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Определяем ответ в зависимости от команды
            String answerText;
            if (userMessageText.equals("/start")) {
                answerText = "Привет! Я эхо-бот. Просто напиши мне что-нибудь.";
            } else if (userMessageText.equals("/help")) {
                answerText = "Я повторю за тобой любое текстовое сообщение.";
            } else {
                answerText = "Эхо: " + userMessageText;
            }

            // Создаем объект ответного сообщения с использованием билдера
            SendMessage reply = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(answerText)
                    .build();

            // Пытаемся отправить сообщение
            try {
                telegramClient.execute(reply);
            } catch (TelegramApiException e) {
                System.err.println("❌ Ошибка обработки сообщения: " + e.getMessage());
                e.printStackTrace();
//                sendText(chatId, "⚠️ Произошла ошибка. Попробуйте еще раз.");
            }
        }
    }
}