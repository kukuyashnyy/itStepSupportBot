package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.Command;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class SystemHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(SystemHandler.class);
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
                bot.sendQueue.add(getMessageUsers(chatId));
                break;
        }
        return "";
    }

    private SendMessage getMessageHelp(String chatID, Integer id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);

        StringBuilder text = new StringBuilder();
        text.append("*Список команд*").append(END_LINE).append(END_LINE);
        text.append("[/start](/start) - показать стартовое сообщение").append(END_LINE);
        text.append("[/help](/help) - показать список команд").append(END_LINE);
        //TODO Добавить после реализации функционала
//        if (bot.userDao.isUser(update.getMessage().getFrom().getId())) {
//            text.append("[/ticket](/ticket) - создать обращение").append(END_LINE);
//        }
        if (bot.userDao.isExist(id)) {
//            if (bot.userDao.isUser(id) && !bot.userDao.isAdmin(id) && !bot.userDao.isMaster(id))
//                text.append("[/register](/register) - отправить запрос на регистрацию").append(END_LINE);
            if (bot.userDao.isAdmin(id)) {
                text.append("[/users](/users) - показать список неавторизированых пользователей").append(END_LINE);
                text.append("[/register_user](/register_user) + user id - зарегистрировать сотрудника").append(END_LINE);
            }
            if (bot.userDao.isMaster(id)) {
                text.append("[/register_admin](/register_admin) + user id - зарегистрировать администратора").append(END_LINE);
            }
        } else {
            text.append("[/register](/register) - отправить запрос на регистрацию").append(END_LINE);
        }

        text.append("[/about_me](/about_me) - узнать информацию о себе").append(END_LINE);

//        text.append("/*notify* _time-in-sec_  - receive notification from me after the specified time").append(END_LINE);

        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageStart(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        StringBuilder text = new StringBuilder();
        text.append("Здравствуйте, я *").append(bot.getBotUsername()).append("*").append(END_LINE);
        text.append("Я создан для помощи сотрудикам Академии Шаг").append(END_LINE);
        text.append("Что бы узнать чем я могу помочь, воспользуйтесь командой [/help](/help)");
        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageAboutMe(String chatID, Integer id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        StringBuilder text = new StringBuilder();

        text.append("Вы ");
        if (bot.userDao.isExist(id)) {
            text.append("зарегистрированы как ");
            if (bot.userDao.isUser(id)) text.append("сотрудник.");
            if (bot.userDao.isAdmin(id) && !bot.userDao.isMaster(id)) text.append("администратор.");
            if (bot.userDao.isMaster(id)) text.append("мастер.");
            if (!bot.userDao.isUser(id) &&
                    !bot.userDao.isAdmin(id) &&
                    !bot.userDao.isMaster(id))
                text.append("не авторизированый пользователь.");
        } else {
            text.append("не зарегистрированы.");
        }
        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageUsers(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        List<User> users = bot.userDao.findAllNotAuthorized();

        if (!users.isEmpty()) {
            for (User user : users) {
                text.append(user.getFirstName()).append(DELIMITER);
                text.append(user.getLastName()).append(DELIMITER);
                text.append(user.getUserId());
            }
        } else {
            text.append("Нет неавторизированых пользователей.");
        }

        sendMessage.setText(text.toString());
        return sendMessage;
    }

}
