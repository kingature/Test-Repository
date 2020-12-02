package statsdisplay.commands.cmd;

import statsdisplay.commands.StatsDisplayCommand;
import statsdisplay.util.Chat;
import statsdisplay.util.MojangAPI;

public class CommandSpell extends StatsDisplayCommand {

    /* Array to store all possible values in each slot */
    String[][] args = {{".spell"}};

    /* Return command name */
    @Override
    public String getName() {
        return ".spell";
    }

    /* Get command usage */
    @Override
    public String[] getCommandUsage() {
        return new String[]{".spell [name]"};
    }

    /* Method that is executed when the command is called */
    @Override
    public void execute(String param) {
        if (param.split("\\s+").length > 2) {
            Chat.msgClient("Too many arguments!. Try .help for more information", Chat.RED);
        } else if (param.split("\\s+").length < 2) {
            Chat.msgClient("Not enough arguments! Try .help for more information", Chat.RED);
        } else {
            String correctSpelling = MojangAPI.getSpelling(param.split("\\s+")[1]);
            if (correctSpelling != null) {
                Chat.msgClient(correctSpelling, Chat.ORANGE);
            } else Chat.msgClient(param.split("\\s+")[1] + " is either a nicked player or misspelled", Chat.RED);
        }
    }
}


