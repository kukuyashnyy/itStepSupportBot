package org.itstep.telegrambot.handler;

import com.mysql.cj.xdevapi.TableImpl;
import org.apache.log4j.Logger;
import org.itstep.domain.entity.Ticket;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DefaultHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger(DefaultHandler.class);
    private static final Integer TELEGRAM_ID = 777000;
    private final String CHANNEL_ID = System.getenv("channel_Id");
    private final String GROUP_ID = System.getenv("group_Id");
    private static enum FromWho {
        USER, ADMIN, TELEGRAM
    }

    public DefaultHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        Ticket ticket;

        switch (analyzeFromWho(userId)) {
            case TELEGRAM:
                ticket = bot.ticketDao.findByDateAndMessageToId(update.getMessage().getForwardDate(),
                        update.getMessage().getForwardFromMessageId());
                if (ticket != null) {
                    ticket.setMessageToId(update.getMessage().getMessageId());
                    bot.ticketDao.update(ticket);
                }
                break;
            case ADMIN:
                log.error(update.toString());
                break;
            case USER:
                ticket = bot.ticketDao.findByUserIdAndOpenedAndNotClosed(userId);
                if (ticket != null) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("" + update.getMessage().getText());
                    sendMessage.setReplyToMessageId(ticket.getMessageToId());
                    sendMessage.setChatId(String.valueOf(GROUP_ID));
                    log.info("Send message: " + sendMessage);
                    bot.sendQueue.add(sendMessage);
                }
                break;
        }
        return "";
    }
    private FromWho analyzeFromWho(Integer userId) {
        if (userId == TELEGRAM_ID) {
            return FromWho.TELEGRAM;
        }
        if (bot.userDao.isAdmin(userId) || bot.userDao.isMaster(userId)) {
            return FromWho.ADMIN;
        } else {
            return FromWho.USER;
        }
    }
}
