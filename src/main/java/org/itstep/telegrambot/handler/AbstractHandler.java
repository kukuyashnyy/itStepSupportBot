package org.itstep.telegrambot.handler;

import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractHandler {
    Bot bot;

    AbstractHandler (Bot bot) {
        this.bot = bot;
    }

    public abstract String operate (String chatId, ParsedCommand parsedCommand, Update update);
}
