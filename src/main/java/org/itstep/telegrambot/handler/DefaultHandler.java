package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.Ticket;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DefaultHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(DefaultHandler.class);
    private static final Integer TELEGRAM_ID = 777000;

    private final String END_LINE = "\n";
    private final String DELIMITER = " ";

    private final String CHANNEL_ID = System.getenv("channel_Id");
    private final String GROUP_ID = System.getenv("group_Id");
    private final String GROUP_ANONYMOUS_BOT = "GroupAnonymousBot";

    private static enum FromWho {
        USER, TELEGRAM, CHANNEL, ANONYMOUS
    }

    public DefaultHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Ticket ticket;
        log.debug(update);
        User user = bot.userDao.findUserById(update.getMessage().getFrom().getId());

        switch (analyzeFrom(update, user)) {
            case TELEGRAM:
                ticket = bot.ticketDao.findByDateAndMessageToId(update.getMessage().getForwardDate(),
                        update.getMessage().getForwardFromMessageId());
                if (ticket != null) {
                    log.info("New ticketId: " + ticket.getId());
                    ticket.setMessageToId(update.getMessage().getMessageId());
                    bot.ticketDao.update(ticket);
                }
                break;
            case CHANNEL:
                ticket = bot.ticketDao.findByMessageToId(update.getMessage().getReplyToMessage().getMessageId());
                //owner of channel
                if (update.getMessage().getFrom().getUserName() != null &&
                        update.getMessage().getFrom().getUserName().equals(GROUP_ANONYMOUS_BOT)) {
                    log.info("Is from owner");
                    sendAnswer(update, ticket);
                    break;
                }
                //admin
                if (user != null && (user.isAdmin() || user.isMaster())) {
                    log.info("Is from admin");
                    sendAnswer(update, ticket);
                } else {
                    if (ticket != null) {
                        bot.sendQueue.add(getMessageToNotAdminFromChannel(ticket));
                    }
                }
                break;
            case USER:
                ticket = bot.ticketDao.findByUserIdAndOpenedAndNotClosed(update.getMessage().getFrom().getId());
                if (ticket != null) {
                    bot.sendQueue.add(getMessageToAdmin(update, ticket));
                } else {
                    bot.sendQueue.add(getMessageTicketNotExist(update));
                }
                break;
            case ANONYMOUS:
                break;
        }
        return "";
    }

    private void sendAnswer(Update update, Ticket ticket) {
        if (ticket != null) {
            if (ticket.isOpened() & !ticket.isClosed()) {
                bot.sendQueue.add(getMessageToUser(update, ticket));
            } else {
                bot.sendQueue.add(getMessageTicketClosed(ticket));
            }
        }
    }

    private FromWho analyzeFrom(Update update, User user) {
        Integer userId = update.getMessage().getFrom().getId();
        if (userId.equals(TELEGRAM_ID)) {
            log.info("Is from telegram");
            return FromWho.TELEGRAM;
        }
        if (update.getMessage().getChatId().equals(Long.parseLong(GROUP_ID))) {
            log.info("Is from channel");
            return FromWho.CHANNEL;
        }
        if (user != null) {
            log.info("Is from user");
            return FromWho.USER;
        } else {
            log.info("Is from anonymous");
            return FromWho.ANONYMOUS;
        }

    }

    private SendMessage getMessageTicketClosed(Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyToMessageId(ticket.getMessageToId());
        sendMessage.setChatId(String.valueOf(GROUP_ID));
        sendMessage.setText("Данное обращение закрыто пользователем.");
        return sendMessage;
    }

    private SendMessage getMessageToNotAdminFromChannel(Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyToMessageId(ticket.getMessageToId());
        sendMessage.setChatId(String.valueOf(GROUP_ID));
        sendMessage.setText("Вы не можете отправлять сообщения пользователям.");
        return sendMessage;
    }

    private SendMessage getMessageToUser(Update update, Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        if (update.getMessage().getText() != null) {
            sendMessage.setChatId(ticket.getUserId().toString());
            sendMessage.setText(update.getMessage().getText());
        } else {
            sendMessage.setText("Сообщение не отправленно.");
            sendMessage.setReplyToMessageId(ticket.getMessageToId());
            sendMessage.setChatId(String.valueOf(GROUP_ID));
        }

        return sendMessage;
    }

    private SendMessage getMessageToAdmin(Update update, Ticket ticket) {
        SendMessage sendMessage = new SendMessage();
        if (update.getMessage().getText() != null) {
            sendMessage.setText(update.getMessage().getText());
            sendMessage.setReplyToMessageId(ticket.getMessageToId());
            sendMessage.setChatId(String.valueOf(GROUP_ID));
        } else {
            sendMessage.setText("Сообщение не отправленно.");
            sendMessage.setChatId(update.getMessage().getChatId().toString());
        }

        return sendMessage;
    }

    private SendMessage getMessageTicketNotExist(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.enableMarkdown(true);

        StringBuilder text = new StringBuilder();
        text.append("Извините, но у вас нет открытых обращений.");
        text.append(END_LINE);
        text.append("Для создания обращения воспользуйтесь коммандой [/ticket](/ticket)");

        sendMessage.setText(text.toString());
        return sendMessage;
    }


}
