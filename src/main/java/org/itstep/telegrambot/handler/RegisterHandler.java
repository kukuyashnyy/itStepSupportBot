package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(RegisterHandler.class);

    public RegisterHandler(Bot bot) {
        super(bot);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Integer fromUserId = update.getMessage().getFrom().getId();
        String text = parsedCommand.getText();

        String response;
        if (bot.userDao.isAdmin(fromUserId)) {
            response = forAdmins(fromUserId, text);
        } else {
            response = forUser(fromUserId, update);
        }
//TODO Cannot invoke "String.equals(Object)" because "response" is null

        return response == null ? "None" : response;
    }

    private String forUser(Integer id, Update update) {
        User user;

        if (!bot.userDao.isExist(id)) {
            user = new User();
            user.setUserId(update.getMessage().getFrom().getId());
            user.setUserName(update.getMessage().getFrom().getUserName());
            user.setFirstName(update.getMessage().getFrom().getFirstName());
            user.setLastName(update.getMessage().getFrom().getLastName());

            bot.userDao.save(user);
            return user.toString();
        }

        if (bot.userDao.isExist(id) &&
                !bot.userDao.isUser(id) &&
                !bot.userDao.isAdmin(id) &&
                !bot.userDao.isUser(id))
            return "You already registered. But admin not accept you.";
        return null;
    }

    private String forAdmins(Integer fromUserId, String text) {
            Integer id = Integer.parseInt(text);
            if (bot.userDao.isExist(id)) {
                if (!bot.userDao.isUser(id)) {
                    User user = bot.userDao.findUserById(id);
                    user.setUser(true);
                    bot.userDao.update(user);
                    return "User registered";
                } else {
                    return "User already exist";
                }
            } else {
                return "User not exist";
            }
    }
}
