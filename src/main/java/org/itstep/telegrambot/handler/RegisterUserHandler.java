package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterUserHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(RegisterUserHandler.class);

    public RegisterUserHandler(Bot bot) {
        super(bot);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        User fromUser = bot.userDao.findUserById(update.getMessage().getFrom().getId());
        Integer id;
        User user;
        try {
            id = Integer.parseInt(parsedCommand.getText());
            user = bot.userDao.findUserById(id);
        } catch (Exception e) {
            return "Введен не верный user id.";
        }
        String text = "Пользователь c id: " + id + ",";

        if (fromUser != null && (fromUser.isAdmin() || fromUser.isMaster())) {
            if (user != null) {
                if (!user.isUser()) {
                    user.setUser(true);
                    user.setAdmin(false);
                    user.setMaster(false);
                    try {
                        bot.userDao.update(user);
                        SendMessage message = new SendMessage();
                        message.setText("Администратор зарегистрировал вас как сотрудника.");
                        message.setChatId(id.toString());
                        bot.sendQueue.add(message);
                        return text + " зарегистрирован как сотрудник.";
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return "Ошибка регистрации пользователя.";
                    }
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
