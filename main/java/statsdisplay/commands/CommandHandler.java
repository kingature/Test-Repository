package statsdisplay.commands;

import statsdisplay.util.Chat;

import java.util.ArrayList;

public class CommandHandler {
    /* A list of all registered commands */
    public static ArrayList<StatsDisplayCommand> registeredCommands;

    /* Constructor for creating a CommandHandler */
    public CommandHandler() {
        registeredCommands = new ArrayList<>();
    }

    /* Method that handles all the incoming messages */
    public boolean checkForCommands(String msg) {
        /* Whether an error message should be printed or not */
        boolean printErrorMessage = true;

        /* If a message starts with a dot, recognize it as a command */
        if (msg.startsWith(".")) {

            /* Check if the message matches a registered command */
            String base = msg.split("\\s+")[0];
            for (StatsDisplayCommand command : registeredCommands) {
                if (base.equalsIgnoreCase(command.getName())) {
                    command.execute(msg);
                    printErrorMessage = false;
                }
            }

            /* Show an error message when typing an unknown or wrong command */
            if (printErrorMessage) {
                String errorMessage = "Unknown command" + Chat.orange(" " + msg.substring(1).trim()) + Chat.RED + ". Try "+  Chat.orange(".help") + " for a list of all commands";
                Chat.msgClient(errorMessage, Chat.RED);
            }

            /* don't show the message if it begins with a . */
            return true;
        }


        /* true -> message will show up, false -> message won't show up */
        return false;
    }

    /* Method that registers a custom command */
    public void registerCommand(StatsDisplayCommand command) {
        registeredCommands.add(command);
    }
}