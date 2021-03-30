package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterHandler extends AbstractHandler{

    private static final Logger log = Logger.getLogger(RegisterUserHandler.class);

    public RegisterHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        if (!bot.userDao.isExist(update.getMessage().getFrom().getId())) {
            User user = new User();
            user.setUserId(update.getMessage().getFrom().getId());
            user.setUserName(update.getMessage().getFrom().getUserName());
            user.setFirstName(update.getMessage().getFrom().getFirstName());
            user.setLastName(update.getMessage().getFrom().getLastName());

            bot.userDao.save(user);
            return "Вы зарегистрированы. Ожидайте подтверждения администратора.";
        } else {
            return "Вы уже зарегистрированы, но администратор не потвердил вашу регистрацию.";
        }
    }
}
