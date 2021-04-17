package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.Ticket;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.Command;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

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
                        bot.sendQueue.add(getMessageStartTicket(chatId, update.getMessage().getFrom()));
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
                        bot.sendQueue.add(getMessageCloseTicketToChannel(ticket));
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
    private SendMessage getMessageStartTicket(String chatId, User user) {
        SendMessage messageToChannel = new SendMessage();
        messageToChannel.setChatId(this.CHANNEL_ID);
        StringBuilder textToChanel = new StringBuilder();

        textToChanel.append("Обращение от пользователя: ");
        textToChanel.append(END_LINE);
        textToChanel.append("User ID: " + user.getId());
        textToChanel.append(END_LINE);
        if (user.getFirstName() != null) {
            textToChanel.append("Имя: " + user.getFirstName());
            textToChanel.append(END_LINE);
        }
        if (user.getLastName() != null) {
            textToChanel.append("Фамилия: " + user.getLastName());
            textToChanel.append(END_LINE);
        }

        messageToChannel.setText(textToChanel.toString());

        StringBuilder textToUser = new StringBuilder();
        SendMessage messageToUser = new SendMessage();
        messageToUser.setChatId(chatId);

        try {
            Message response = bot.execute(messageToChannel);

            createTicket(response, user.getId());
            textToUser.append("Ваше обращение успешно создано.");
            textToUser.append(END_LINE);
            textToUser.append("Для начала общения с администратором, напишите в чат.");

            messageToUser.setText("Ваше обращение успешно зарегистрировано.");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            textToUser.append("Ошибка регистрации обращения. Попробуйте еще раз.");
        }
        messageToUser.setText(textToUser.toString());
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
    private SendMessage getMessageCloseTicketToChannel(Ticket ticket) {
        SendMessage commentMessage = new SendMessage();
        commentMessage.setChatId(GROUP_ID);
        commentMessage.setText("Пользователь закрыл обращение.");
        commentMessage.setReplyToMessageId(ticket.getMessageToId());
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