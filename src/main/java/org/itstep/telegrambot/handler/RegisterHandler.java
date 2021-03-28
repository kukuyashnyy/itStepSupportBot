package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.App1;
import org.itstep.domain.entity.User;
import org.itstep.repository.UserRepository;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger(RegisterHandler.class);

    public RegisterHandler(Bot bot) {
        super(bot);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        if (bot.userDao.findUserByUserId(update.getMessage().getFrom().getId()) != null) {
            return "You already registered.";
        } else {
            User user = new User();
            user.setUserId(update.getMessage().getFrom().getId());
            user.setUserName(update.getMessage().getFrom().getUserName());
            user.setFirstName(update.getMessage().getFrom().getFirstName());
            user.setLastName(update.getMessage().getFrom().getLastName());

            bot.userDao.save(user);
            return user.toString();
        }
    }
}
