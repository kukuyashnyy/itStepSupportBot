package org.itstep.telegrambot.service;

import org.apache.log4j.Logger;
import org.itstep.telegrambot.Bot;
import org.itstep.telegrambot.command.Command;
import org.itstep.telegrambot.command.ParsedCommand;
import org.itstep.telegrambot.command.Parser;
import org.itstep.telegrambot.handler.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceiver implements Runnable {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    private Bot bot;
    private Parser parser;

    public MessageReceiver(Bot bot) {
        this.bot = bot;
        parser = new Parser(bot.getBotUsername());
    }

    @Override
    public void run() {
        log.info("[STARTED] MsgReciever.  Bot class: " + bot);
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                log.debug("New object for analyze in queue " + object.toString());
                analyze(object);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    private void analyze(Object object) {
        if (object instanceof Update) {
            Update update = (Update) object;
            log.debug("Update recieved: " + update.toString());
            analyzeForUpdateType(update);
        } else log.warn("Cant operate type of object: " + object.toString());
    }

    private void analyzeForUpdateType(Update update) {
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        ParsedCommand parsedCommand = parser.getParsedCommand(inputText);
        AbstractHandler handlerForCommand = getHandlerForCommand(parsedCommand.getCommand(), update);

        String operationResult = handlerForCommand.operate(chatId.toString(), parsedCommand, update);

        if (!"".equals(operationResult)) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(operationResult);
            bot.sendQueue.add(message);
        }
    }

    private AbstractHandler getHandlerForCommand(Command command, Update update) {
        Integer id = update.getMessage().getFrom().getId();
        if (command == null) {
            log.warn("Null command accepted. This is not good scenario.");
            return new DefaultHandler(bot);
        }


        switch (command) {
            case START:
            case HELP:
            case USERS:
            case ABOUT_ME:
                SystemHandler systemHandler = new SystemHandler(bot);
                log.info("Handler for command[" + command.toString() + "] is: " + systemHandler);
                return systemHandler;
//            case NOTIFY:
//                NotifyHandler notifyHandler = new NotifyHandler(bot);
//                log.info("Handler for command[" + command.toString() + "] is: " + notifyHandler);
//                return notifyHandler;
            case REGISTER:
                RegisterHandler registerHandler = new RegisterHandler(bot);
                log.info("Handler for command[" + command.toString() + "] is: " + registerHandler);
                return registerHandler;
            case REGISTER_USER:
                RegisterUserHandler registerUserHandler = new RegisterUserHandler(bot);
                log.info("Handler for command[" + command.toString() + "] is: " + registerUserHandler);
                return registerUserHandler;
            case REGISTER_ADMIN:
                RegisterAdminHandler registerAdminHandler = new RegisterAdminHandler(bot);
                log.info("Handler for command[" + command.toString() + "] is: " + registerAdminHandler);
                return registerAdminHandler;
            default:
                log.info("Handler for command[" + command.toString() + "] not Set. Return DefaultHandler");
                return new DefaultHandler(bot);
        }
    }
}
