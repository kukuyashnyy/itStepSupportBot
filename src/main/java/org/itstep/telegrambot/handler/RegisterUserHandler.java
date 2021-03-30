package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterUserHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(RegisterUserHandler.class);

    public RegisterUserHandler(Bot bot) {
        super(bot);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Integer fromUserId = update.getMessage().getFrom().getId();
        Integer id = Integer.parseInt(parsedCommand.getText());
        String text = "Пользователь c id: " + id + ",";

        String response;
        if (bot.userDao.isAdmin(fromUserId)) {
            if (bot.userDao.isExist(id)) {
                if (!bot.userDao.isUser(id)) {
                    User user = bot.userDao.findUserById(id);
                    user.setUser(true);
                    bot.userDao.update(user);
                    return text + " зарегистрирован.";
                } else {
                    return text + " уже зарегистрирован.";
                }
            } else {
                return text + " не найден.";
            }
        } else {
            return "Извините, вы не можете воспользоваться данной командой.";
        }
    }
}
