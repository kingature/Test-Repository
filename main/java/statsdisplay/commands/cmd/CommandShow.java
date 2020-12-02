package statsdisplay.commands.cmd;

import com.google.gson.JsonParser;
import statsdisplay.commands.StatsDisplayCommand;
import statsdisplay.main.StatsDisplay;
import statsdisplay.util.Chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class CommandShow extends StatsDisplayCommand {
    /* Array to store all possible values in each slot */
    String[][] args = {{".show"}};

    /* Return command name */
    @Override
    public String getName() {
        return ".show";
    }

    /* Get command usage */
    @Override
    public String[] getCommandUsage() {
        return new String[]{".show"};
    }

    /* Method that is executed when the command is called */
    @Override
    public void execute(String param) {
        if (!param.trim().equals(getName())) {
            Chat.msgClient("Too many arguments! Try .help for more information", Chat.RED);
            return;
        }
        StatsDisplay.playerList.show();

        String sURL = "https://api.mojang.com/users/profiles/minecraft/adshiaiihhshd"; //just a string

        try {
            new URL(sURL).openConnection().connect();
            String playerName = new JsonParser().parse(new InputStreamReader((InputStream) new URL(sURL).openConnection().getContent())).getAsJsonObject().get("name").getAsString(); //May be an array, may be an object.
            Chat.msgClient(playerName, Chat.RED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
