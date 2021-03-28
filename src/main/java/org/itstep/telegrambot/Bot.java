package org.itstep.telegrambot;

import org.apache.log4j.Logger;
import org.itstep.domain.dao.Impl.UserDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.PersistenceContext;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Bot extends TelegramLongPollingBot {

    public static final Logger log = Logger.getLogger(Bot.class);
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();

    public final UserDaoImpl userDao = new UserDaoImpl();

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
    }
}
