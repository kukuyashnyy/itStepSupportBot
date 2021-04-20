package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterHandler extends AbstractHandler {

    private static final Logger log = Logger.getLogger(RegisterUserHandler.class);

    public RegisterHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        User user = bot.userDao.findUserById(update.getMessage().getFrom().getId());
        if (user == null) {
                try {
                    user = new User();
                    user.setUserId(update.getMessage().getFrom().getId());
                    user.setUserName(update.getMessage().getFrom().getUserName());
                    user.setFirstName(update.getMessage().getFrom().getFirstName());
                    user.setLastName(update.getMessage().getFrom().getLastName());
                    user.setPhone(update.getMessage().getContact().getPhoneNumber());
                    bot.userDao.save(user);
                    return "Вы зарегистрированы. Ожидайте подтверждения администратора.";
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return "Извините, произошла ошибка. Повторите попытку позже.";
                }
            }
        if (user.isUser() || user.isAdmin() || user.isMaster()) {
            return "Вы уже зарегистрированы.";
        } else {
            return "Вы уже зарегистрированы, но администратор не потвердил вашу регистрацию.";
        }
    }
}
