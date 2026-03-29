package ru.university.textbot;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.university.textbot.config.BotConfig;
import ru.university.textbot.processor.TextProcessor;

public class TextBotApplication {
    public static void main(String[] args) {

        try {
            // Создаем экземпляр бота
            TextProcessor quizBot = new TextProcessor();

            // Создаем приложение Long Polling
            TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();

            // Регистрируем бота
            botsApplication.registerBot(BotConfig.BOT_TOKEN, quizBot);

            System.out.println("✅ Бот успешно запущен!");
            System.out.println("🤖 Имя бота: @" + BotConfig.BOT_USERNAME);
            System.out.println("📊 Ожидание сообщений...");
            System.out.println("Для остановки нажмите Ctrl+C");

            // Бесконечный цикл для работы приложения
            Thread.currentThread().join();

        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка Telegram API: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("👋 Бот остановлен");
        } catch (Exception e) {
            System.err.println("❌ Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
