package org.itstep.telegrambot.command;

import org.apache.log4j.Logger;
import java.util.AbstractMap;


public class Parser {
    private static final Logger log = Logger.getLogger(Parser.class);
    private final String PREFIX_FOR_COMMAND = "/";
    private final String DELIMITER_COMMAND_BOTNAME = "@";
    private String botName;

    public Parser(String botName) {
        this.botName = botName;
    }

    public ParsedCommand getParsedCommand(String text) {
        String trimText = "";
        if (text != null) trimText = text.trim();
        ParsedCommand result = new ParsedCommand(Command.NONE, trimText);

        if ("".equals(trimText)) return result;
        AbstractMap.SimpleEntry<String, String> commandAndText = getDelimitedCommandFromText(trimText);
        if (isCommand(commandAndText.getKey())) {
            if (isCommandForMe(commandAndText.getKey())) {
                String commandForParse = cutCommandFromFullText(commandAndText.getKey());
                Command commandFromText = getCommandFromText(commandForParse);
                result.setText(commandAndText.getValue());
                result.setCommand(commandFromText);
            } else {
                result.setCommand(Command.NOTFORME);
                result.setText(commandAndText.getValue());
            }
        }
        return result;
    }

    private Command getCommandFromText(String text) {
        String upperCaseText = text.toUpperCase().trim();
        Command command = Command.NONE;
        try {
            command = Command.valueOf(upperCaseText);
        } catch (IllegalArgumentException e) {
            log.debug("Cant parse command: " + text);
        }
        return command;
    }

    private String cutCommandFromFullText(String key) {
        return key.contains(DELIMITER_COMMAND_BOTNAME) ?
                key.substring(1, key.indexOf(DELIMITER_COMMAND_BOTNAME)) :
                key.substring(1);
    }

    private boolean isCommandForMe(String key) {
        if (key.contains(DELIMITER_COMMAND_BOTNAME)) {
            String botNameForEqual = key.substring(key.indexOf(DELIMITER_COMMAND_BOTNAME) + 1);
            return botName.equals(botNameForEqual);
        }
        return true;
    }

    private boolean isCommand(String key) {
        return key.startsWith(PREFIX_FOR_COMMAND);
    }

    private AbstractMap.SimpleEntry<String, String> getDelimitedCommandFromText(String trimText) {
        AbstractMap.SimpleEntry<String, String> commandText;

        if (trimText.contains(" ")) {
            int indexOfSpace = trimText.indexOf(" ");
            commandText = new AbstractMap.SimpleEntry<>(trimText.substring(0, indexOfSpace),
                    trimText.substring(indexOfSpace + 1));

        } else commandText = new AbstractMap.SimpleEntry<>(trimText, "");
        return commandText;
    }
}
