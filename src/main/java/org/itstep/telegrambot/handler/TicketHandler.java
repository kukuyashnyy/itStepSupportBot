package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.Ticket;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.Command;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TicketHandler extends AbstractHandler{

    private static final Logger log = Logger.getLogger(SystemHandler.class);
    private final String END_LINE = "\n";
    private final String CHANNEL_ID = System.getenv("channel_Id");
    private final String GROUP_ID = System.getenv("group_Id");

    public TicketHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();
        Integer userId = update.getMessage().getFrom().getId();
        Ticket ticket;
        if(bot.userDao.isRegistered(userId)) {
            ticket = bot.ticketDao.findByUserIdAndOpenedAndNotClosed(userId);
            switch (command) {
                case TICKET:
                    if (ticket == null) {
                        bot.sendQueue.add(getMessageStartTicket(chatId, userId));
                    } else {
                        bot.sendQueue.add(getMessageTicketIsExist(chatId));
                    }
                    break;

                case CLOSE_TICKET:
                    if (ticket == null) {
                        bot.sendQueue.add(getMessageTicketNotExist(chatId));
                    } else {
                        ticket.setClosed(true);
                        bot.ticketDao.update(ticket);
                        bot.sendQueue.add(getMessageCloseTicketToChannel(userId));
                        bot.sendQueue.add(getMessageCloseTicketToUser(chatId));
                    }
                    break;
            }
        } else {
            bot.sendQueue.add(getMessageNotRegistered(update.getMessage().getChatId().toString()));
        }
        return "";
    }

    private SendMessage getMessageTicketIsExist(String chatId) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);

        StringBuilder text = new StringBuilder();
        text.append( "Обращение уже зарегистрировано.");
        text.append(END_LINE);
        text.append("Для завершения обращения воспользуйтесь командой");
        text.append(END_LINE);
        text.append("[/close_ticket](/close_ticket)");

        message.setChatId(chatId);
        message.setText(text.toString());
        return message;
    }
    private SendMessage getMessageStartTicket(String chatId, Integer userId) {
        SendMessage messageToChannel = new SendMessage();
        messageToChannel.setChatId(this.CHANNEL_ID);
        messageToChannel.setText("New ticked from: " + userId);

        SendMessage messageToUser = new SendMessage();
        messageToUser.setChatId(chatId);

        try {
            Message response = bot.execute(messageToChannel);
            createTicket(response, userId);
            messageToUser.setText("Ваше обращение успешно зарегистрировано.");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            messageToUser.setText("Ошибка регистрации обращения. Попробуйте еще раз.");
        }
        return messageToUser;
    }
    private void createTicket(Message message, Integer userId) {
        Ticket ticket = new Ticket();
        ticket.setMessageFromId(message.getMessageId());
        ticket.setMessageDate(message.getDate());
        ticket.setUserId(userId);
        ticket.setOpened(true);
        bot.ticketDao.save(ticket);
    }
    private SendMessage getMessageTicketNotExist(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("У вас нет активного обращения.");
        return message;
    }
    private SendMessage getMessageCloseTicketToChannel(Integer userId) {
        SendMessage commentMessage = new SendMessage();
        commentMessage.setChatId(GROUP_ID);
        commentMessage.setText("User: " + userId + " close ticket.");
        commentMessage.setReplyToMessageId(bot.ticketDao.findByUserId(userId).getMessageToId());
        return commentMessage;
    }
    private SendMessage getMessageCloseTicketToUser(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Ваше обращение было закрыто.");
        return message;
    }
    private SendMessage getMessageNotRegistered(String chatId) {
        SendMessage message = new SendMessage();
        message.setText("Только зарегистрированные пользователи могут создавать или закрывать обращения.");
        message.setChatId(chatId);
        return message;
    }
}
