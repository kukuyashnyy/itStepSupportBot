package org.itstep.telegrambot.handler;

import org.apache.log4j.Logger;
import org.itstep.domain.entity.User;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RegisterAdminHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(RegisterAdminHandler.class);

    public RegisterAdminHandler(Bot bot) {
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

        if (fromUser != null && fromUser.isMaster()) {
            if (user != null) {
                if (!user.isAdmin()) {
                    user.setUser(false);
                    user.setAdmin(false);
                    user.setMaster(true);
                    try {
                        bot.userDao.update(user);
                        SendMessage message = new SendMessage();
                        message.setText("Мастер зарегистрировал вас как администратора.");
                        message.setChatId(id.toString());
                        bot.sendQueue.add(message);
                        return text + " зарегистрирован как администратор";
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return "Ошибка регистрации администратора.";
                    }
                } else {
                    return text + " уже зарегистрирован как администратор.";
                }
            } else {
                return text + " не найден.";
            }
        } else {
            return "Извините, вы не можете воспользоваться данной командой.";
        }
    }
}
