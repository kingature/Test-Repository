package statsdisplay.commands;

import statsdisplay.util.Chat;

public abstract class StatsDisplayCommand {
    /* Return command name */
    public String getName() {
        return null;
    }

    /* Get command usage */
    public String[] getCommandUsage() {
        return null;
    }

    /* Method that is executed when the command is called */
    public void execute(String param) {

    }

    /* Checks whether a command has the correct syntax and returns a
    string array when it is. [0] -> command without parameter, [1] ->
    parameter */

    /* Array to store all possible values in each slot */
    public String[][] args;

    public String[] validate(String param, String[][] arguments) {
        /* Initialize both args and finalCommand */
        args = arguments;

        /* Array to store each part of the final command */
        String[] finalCommand = new String[args.length];

        StringBuilder errorMessage = new StringBuilder();
        /* Array to represent the incoming message in its peaces */

        String[] parts = param.split("\\s+");
        if (parts.length - 1 != args.length) {
            /* Command can't be correct */
            if (parts.length - 1 < args.length) {
                Chat.msgClient("Missing arguments! Try .help for more information", Chat.RED);
            } else Chat.msgClient("Too many arguments! Try .help for more information", Chat.RED);
            return null;
        }

        for (int i = 0; i < parts.length - 1; i++) {
            if (contains(args[i], parts[i])) {
                finalCommand[i] = parts[i];
            } else {
                errorMessage.append("Unknown parameter `").append(parts[i]).append("`.");
                /* Case args[i] == 0 missing */
                if (args[i].length == 1) {
                    errorMessage.append(" Use ").append(args[i][0]).append(" instead or try .help for more information");
                } else {
                    StringBuilder b = new StringBuilder();
                    for (int j = 0; j < args[i].length - 2; j++) {
                        b.append("`").append(args[i][j]).append("`, ");
                    }
                    b.append("`").append(args[i][args[i].length - 2]).append("` or ");
                    b.append("`").append(args[i][args[i].length - 1]).append("`");
                    errorMessage.append(" Use ").append(b.toString()).append(" instead or try .help for more information");
                    Chat.msgClient(errorMessage.toString(), Chat.RED);
                    return null; /* Command can't be correct */
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (String s : finalCommand) {
            builder.append(s).append(" ");
        }
        String command = builder.toString();
        String argument = parts[parts.length - 1];
        return new String[]{command, argument};
    }

    private boolean contains(String[] a, String b) {
        for (String s : a) {
            if (s.equals(b)) {
                return true;
            }
        }
        return false;
    }
}
