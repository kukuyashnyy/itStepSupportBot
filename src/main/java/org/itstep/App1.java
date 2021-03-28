package org.itstep;


import org.apache.log4j.Logger;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.service.MessageReceiver;
import org.itstep.telegrambot.service.MessageSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class App1
{
    public static final Logger log = Logger.getLogger(App1.class);
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    public static void main( String[] args )
    {
        System.getProperties().put( "proxySet", "true" );

        log.info("Start bot");
        Bot bot = new Bot();

        MessageReceiver messageReceiver = new MessageReceiver(bot);
        MessageSender messageSender = new MessageSender(bot);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        Thread receiver = new Thread(messageReceiver);
        receiver.setDaemon(true);
        receiver.setName("MsgReceiver");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();


    }
}
