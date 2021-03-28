package org.itstep.telegrambot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Bot extends TelegramLongPollingBot {

//    bot_token=1760881491:AAHF1gzJFJnAqzgYA4WVTJVyL2WGRGg4eNA;bot_name=test_kva_bot

    public static final Logger log = Logger.getLogger(Bot.class);
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();

    private final String BOT_TOKEN = System.getenv("bot_token");
    private final String BOT_NAME = System.getenv("bot_name");


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received update. UpdateID: " + update.getUpdateId());
        receiveQueue.add(update);

//        String chatId = update.getMessage().getChatId().toString();
//        String inputText = update.getMessage().getText();
//
//        if (inputText.startsWith("/start")) {
//            SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            message.setText("Hello. This is start message");
//            try {
//                execute(message);
//            } catch (TelegramApiException e) {
//                log.error(e.getLocalizedMessage(), e);
//            }
//        }

//        if (update.hasMessage() && update.getMessage().hasText()) {
//            Message receiveMessage = update.getMessage();
//            log.debug("Message text : " + receiveMessage.getText());
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.setChatId(String.valueOf(receiveMessage.getChatId()));
//            sendMessage.setText("bot say" + update.getMessage().getText());
//            try {
//                execute(sendMessage);
//            } catch (TelegramApiException e) {
//                log.error(e.getLocalizedMessage(), e);
//            }
//        }
    }
}
