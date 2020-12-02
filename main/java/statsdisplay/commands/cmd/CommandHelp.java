package statsdisplay.commands.cmd;

import statsdisplay.commands.CommandHandler;
import statsdisplay.commands.StatsDisplayCommand;
import statsdisplay.util.Chat;

public class CommandHelp extends StatsDisplayCommand {
    /* Array to store all possible values in each slot */
    String[][] args = {{".help"}};

    @Override
    /* Name of the command */
    public String getName() {
        return ".help";
    }

    @Override
    /* String array that will show up in chat when typing .help */
    public String[] getCommandUsage() {
        return new String[]{".help"};
    }

    @Override
    /* Method which will be executed when running the command */
    public void execute(String param) {
        if (!param.trim().equalsIgnoreCase(getName())) {
            Chat.msgClient("Too many arguments! Try .help for more information", Chat.RED);
            return;
        }
        Chat.msgClient("","", false);
        Chat.msgClient("The following commands are available:", Chat.GREEN);
        Chat.msgClient("","", false);

        for (StatsDisplayCommand command : CommandHandler.registeredCommands) {
            String[] message = command.getCommandUsage();
            String space = " ";
            for (String m : message) {
                Chat.msgClient(space + m, Chat.GREEN, false);
            }
        }

        Chat.msgClient("","", false);
    }
}
