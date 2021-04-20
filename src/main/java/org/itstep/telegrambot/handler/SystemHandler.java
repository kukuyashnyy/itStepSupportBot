package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.Ticket;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.Command;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class SystemHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(SystemHandler.class);
    private final String CHANNEL_ID = System.getenv("channel_Id");
    private final String GROUP_ID = System.getenv("group_Id");
    private final String END_LINE = "\n";
    private final String DELIMITER = " ";

    public SystemHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();

        switch (command) {
            case START:
                bot.sendQueue.add(getMessageStart(chatId));
                break;
            case HELP:
                bot.sendQueue.add(getMessageHelp(chatId, update.getMessage().getFrom().getId()));
                break;
            case ABOUT_ME:
                bot.sendQueue.add(getMessageAboutMe(chatId, update.getMessage().getFrom().getId()));
                break;
            case USERS:
                bot.sendQueue.add(getMessageUsers(chatId, update.getMessage().getFrom().getId()));
                break;
/*            case SEND_TO_CHANNEL:
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(CHANNEL_ID));
                message.setText("User id: " + update.getMessage().getFrom().getId().toString());
                try {
                    Message response = bot.execute(message);

                    Ticket ticket = new Ticket();
                    ticket.setMessageFromId(response.getMessageId());
                    ticket.setMessageDate(response.getDate());
                    ticket.setUserId(update.getMessage().getFrom().getId());
                    ticket.setOpened(true);
                    bot.ticketDao.save(ticket);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case SEND_TO_DISCUSS:
                Integer userId = update.getMessage().getFrom().getId();
                SendMessage message1 = new SendMessage();
                message1.setReplyToMessageId(bot.ticketDao.findByUserId(userId).getMessageToId());
                message1.setChatId(String.valueOf(GROUP_ID));
                message1.setText("Reply to chat: " + message1.getReplyToMessageId());
                bot.sendQueue.add(message1);
                break;*/
        }
        return "";
    }

    //TODO проверить отображение команд

    private SendMessage getMessageHelp(String chatID, Integer id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);

        User user = bot.userDao.findById(id);

        StringBuilder text = new StringBuilder();
        text.append("*Список команд*").append(END_LINE).append(END_LINE);
        text.append("[/start](/start) - показать стартовое сообщение").append(END_LINE);
        text.append("[/help](/help) - показать список команд").append(END_LINE);

        if (user != null) {
            if (user.isUser()) {
                text.append("[/ticket](/ticket) - создать обращение").append(END_LINE);
                text.append("[/close_ticket](/close_ticket) - закрыть обращение").append(END_LINE);
            }
            if (user.isAdmin() || user.isMaster()) {
                text.append("[/users](/users) - показать список неавторизированых пользователей").append(END_LINE);
                text.append("[/register_user](/register_user) + user id - зарегистрировать сотрудника").append(END_LINE);
            }
            if (user.isMaster()) {
                text.append("[/register_admin](/register_admin) + user id - зарегистрировать администратора").append(END_LINE);
            }
        } else {
//            text.append("[/register](/register) - отправить запрос на регистрацию").append(END_LINE);
        }

        text.append("[/about_me](/about_me) - узнать информацию о себе").append(END_LINE);

//        text.append("/*notify* _time-in-sec_  - receive notification from me after the specified time").append(END_LINE);

        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(remove);

        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageStart(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        StringBuilder text = new StringBuilder();
        text.append("Здравствуйте, я ").
                append("*").
                append(bot.getBotUsername()).
                append(".").
                append("*").
                append(END_LINE);
        text.append("Я создан для помощи сотрудикам Академии Шаг.").append(END_LINE);
        text.append("Что бы узнать чем я могу помочь, воспользуйтесь командой [/help](/help)");
        sendMessage.setText(text.toString());

        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Зарегистрироваться");
        keyboardButton.setRequestContact(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);
        keyboard.add(keyboardRow);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboard);

        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }

    private SendMessage getMessageAboutMe(String chatID, Integer userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        StringBuilder text = new StringBuilder();

        User user = bot.userDao.findUserById(userId);

        text.append("Вы ");
        if (user != null) {
            text.append("зарегистрированы как ");
            if (user.isUser()) {
                text.append("сотрудник.");
            }
            if (user.isAdmin() & !user.isMaster()) {
                text.append("администратор.");
            }
            if (user.isMaster()) {
                text.append("мастер.");
            }
            if (!user.isUser() & !user.isAdmin() & !user.isMaster()) {
                text.append("не авторизированый пользователь.");
            }
        } else {
            text.append("не зарегистрированы.");
        }

        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(remove);

        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageUsers(String chatID, Integer userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        List<User> users = bot.userDao.findAllNotAuthorized();

        User fromUser = bot.userDao.findUserById(userId);

        if (fromUser != null && (fromUser.isAdmin() || fromUser.isMaster())) {
            if (!users.isEmpty()) {
                for (User user : users) {
                    if (user.getFirstName() != null) text.append(user.getFirstName()).append(DELIMITER);
                    if (user.getLastName() != null) text.append(user.getLastName()).append(DELIMITER);
                    text.append(user.getUserId()).append(END_LINE);
                }
            } else {
                text.append("Нет неавторизированых пользователей.");
            }
        } else {
            text.append("Извините, вы не можете воспользоваться данной командой.");
        }

        sendMessage.setText(text.toString());
        return sendMessage;
    }

}
