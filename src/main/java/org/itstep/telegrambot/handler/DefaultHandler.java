package org.itstep.telegrambot.handler;

import com.mysql.cj.xdevapi.TableImpl;
import org.apache.log4j.Logger;
import org.itstep.domain.entity.Ticket;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class DefaultHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger(DefaultHandler.class);
    private static final Integer TELEGRAM_ID = 777000;
    private final String CHANNEL_ID = System.getenv("channel_Id");
    private final String GROUP_ID = System.getenv("group_Id");
    private final String GROUP_ANONYMOUS_BOT = "GroupAnonymousBot";
    private static enum FromWho {
        USER, TELEGRAM, GROUP_BOT
    }

    public DefaultHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        User user = update.getMessage().getFrom();
        Ticket ticket;

        switch (analyzeFromWho(user)) {
            case TELEGRAM:
                ticket = bot.ticketDao.findByDateAndMessageToId(update.getMessage().getForwardDate(),
                        update.getMessage().getForwardFromMessageId());
                if (ticket != null) {
                    log.info(ticket);
                    ticket.setMessageToId(update.getMessage().getMessageId());
                    bot.ticketDao.update(ticket);
                }
                break;
            case GROUP_BOT:
                log.info(update);
                ticket = bot.ticketDao.findByMessageToId(update.getMessage().getReplyToMessage().getMessageId());
                if (ticket != null) {
                    if (ticket.isOpened() & !ticket.isClosed()) {
                        bot.sendQueue.add(getMessageToUser(update, ticket));
                    } else {
                        bot.sendQueue.add(getMessageTicketClosed(ticket));
                    }
                }
                break;
            case USER:
                ticket = bot.ticketDao.findByUserIdAndOpenedAndNotClosed(user.getId());
                if (ticket != null) {
                    bot.sendQueue.add(getMessageToAdmin(update, ticket));
                }
                break;
        }
        return "";
    }
    private FromWho analyzeFromWho(User user) {
        log.info("User " + user);
        if (user.getId().equals(TELEGRAM_ID)) {
            log.info("Is telegram");
            return FromWho.TELEGRAM;
        }
        if (user.getUserName().equals(GROUP_ANONYMOUS_BOT)){
            log.info("Is GroupAnonymousBot");
            return FromWho.GROUP_BOT;
        } else {
            log.info("Is user");
            return FromWho.USER;
        }
    }
    private SendMessage getMessageTicketClosed(Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyToMessageId(ticket.getMessageToId());
        sendMessage.setChatId(String.valueOf(GROUP_ID));
        sendMessage.setText("Данное обращение закрыто пользователем.");
        return sendMessage;
    }
    private SendMessage getMessageToUser(Update update, Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ticket.getUserId().toString());
        sendMessage.setText(update.getMessage().getText());
        return sendMessage;
    }
    private SendMessage getMessageToAdmin(Update update, Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("" + update.getMessage().getText());
        sendMessage.setReplyToMessageId(ticket.getMessageToId());
        sendMessage.setChatId(String.valueOf(GROUP_ID));
        return sendMessage;
    }
}
