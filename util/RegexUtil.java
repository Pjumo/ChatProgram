package chat.util;

import java.util.Arrays;

public class RegexUtil {

    private static final String REGEX = "\\|";

    public static Command getCommand(String userCommand){
        String command = userCommand.split(REGEX)[0];
        return switch (command) {
            case "/join" -> Command.JOIN;
            case "/message" -> Command.MESSAGE;
            case "/change" -> Command.CHANGE;
            case "/users" -> Command.USERS;
            case "/exit" -> Command.EXIT;
            default -> null;
        };
    }

    public static String writeCommand(Command command) {
        return switch (command){
            case JOIN -> "/join|";
            case MESSAGE -> "/message|";
            case CHANGE -> "/change|";
            case USERS -> "/users|";
            case EXIT -> "/exit";
        };
    }

    public static String getDetail(String userCommand){
        return userCommand.split(REGEX)[1];
    }

    public static String[] getUsers(String userCommand){
        String[] users = userCommand.split(REGEX);
        return Arrays.copyOfRange(users, 1, users.length);
    }
}
