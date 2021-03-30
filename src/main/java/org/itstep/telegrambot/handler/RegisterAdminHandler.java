package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterAdminHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(RegisterAdminHandler.class);

    public RegisterAdminHandler(Bot bot) {
        super(bot);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Integer fromUserId = update.getMessage().getFrom().getId();
        //TODO try for parse
        Integer id = Integer.parseInt(parsedCommand.getText());
        String text = "Пользователь: " + update.getMessage().getFrom().getUserName();

        String response;
        if (bot.userDao.isMaster(fromUserId)) {
            if (bot.userDao.isExist(id)) {
                if (!bot.userDao.isAdmin(id)) {
                    User user = bot.userDao.findUserById(id);
                    user.setAdmin(true);
                    bot.userDao.update(user);
                    return text + " зарегистрирован, как администратор.";
                } else {
                    return text + " уже зарегистрирован, как администратор.";
                }
            } else {
                return text + "не найден.";
            }
        } else {
            return "Извините, вы не можете воспользоваться данной командой.";
        }
    }
}
