package org.itstep.telegrambot;

import org.apache.log4j.Logger;
import org.itstep.App1;
import org.itstep.domain.dao.Impl.TicketDaoImpl;
import org.itstep.domain.dao.Impl.UserDaoImpl;
import org.itstep.repository.TicketRepository;
import org.itstep.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Bot extends TelegramLongPollingBot {

    public static final Logger log = Logger.getLogger(Bot.class);
    public final ConfigurableApplicationContext context = SpringApplication.run(App1.class);

    private final UserRepository userRepository = context.getBean(UserRepository.class);
    private final TicketRepository ticketRepository = context.getBean(TicketRepository.class);

    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();

    public final UserDaoImpl userDao = new UserDaoImpl(userRepository);
    public final TicketDaoImpl ticketDao = new TicketDaoImpl(ticketRepository);

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
