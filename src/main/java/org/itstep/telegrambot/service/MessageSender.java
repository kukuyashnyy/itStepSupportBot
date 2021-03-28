package org.itstep.telegrambot.service;

import org.apache.log4j.Logger;
import org.itstep.telegrambot.Bot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageSender implements Runnable {
    private static final Logger log = Logger.getLogger(MessageSender.class);
    private final int SENDER_SLEEP_TIME = 1000;
    private Bot bot;

    public MessageSender(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info("[STARTED] MsgSender.  Bot class: " + bot);
        try {
            while (true) {
                for (Object object = bot.sendQueue.poll(); object != null; object = bot.sendQueue.poll()) {
                    log.debug("Get new msg to send " + object);
                    send(object);
                }
                try {
                    Thread.sleep(SENDER_SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error("Take interrupt while operate msg list", e);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void send(Object object) {
        try {
            BotApiMethod<Message> message = (BotApiMethod<Message>) object;
            log.debug("Use Execute for " + object);
            bot.execute(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
