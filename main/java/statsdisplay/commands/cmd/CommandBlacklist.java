package statsdisplay.commands.cmd;

import net.labymod.main.LabyMod;
import statsdisplay.commands.StatsDisplayCommand;
import statsdisplay.main.StatsDisplay;
import statsdisplay.util.*;
import java.util.ArrayList;

public class CommandBlacklist extends StatsDisplayCommand {
    /* Array to store all possible values in each slot */
    private final String[][] args = {{".blacklist"}, {"add", "remove"}, {"player", "clan"}};

    /* Lists to store all the blacklisted players and clans */
    public static ArrayList<String> blacklistedClans = new ArrayList<>();
    public static MojangPlayerList blacklistedPlayers = new MojangPlayerList();
    public static ArrayList<String> blacklistedPlayersBuffer = new ArrayList<>();

    @Override
    /* Name of the command */
    public String getName() {
        return ".blacklist";
    }

    @Override
    /* String array that will show up in chat when typing .help */
    public String[] getCommandUsage() {
        return new String[]{".blacklist <add:remove> <player:clan> [name]",
        };
    }

    @Override
    /* Method which will be executed when running the command */
    public void execute(String param) {
        String[] verifiedCommand = validate(param, args);

        /* verifiedCommand layout:
        * [0] blacklist add:remove player:clan
        * [1] argument */

        if (verifiedCommand != null) { /* Is null when the syntax was incorrect */
            String[] parts = verifiedCommand[0].split("\\s+");
            String argument = verifiedCommand[1];

            /* Do stuff */
            if (parts[1].equalsIgnoreCase("add")) {
                if (parts[2].equalsIgnoreCase("player")) {
                    addPlayer(argument);
                } else {
                    addClan(argument);
                }
            } else {
                if (parts[2].equalsIgnoreCase("player")) {
                    removePlayer(argument);
                } else {
                    removeClan(argument);
                }
            }
        }
    }

    /* Add a player to the blacklist */
    private void addPlayer(final String inputPlayer) {
        String correctSpelling = StatsDisplay.playerList.getSpelling(inputPlayer);

        /* Check if you want to blacklist yourself */
        String thisUser = LabyMod.getInstance().getGameProfile().getName();
        if (correctSpelling.equalsIgnoreCase(thisUser)) {
            String errorMessage = "You can't blacklist yourself!";
            Chat.msgClient(errorMessage, Chat.RED);
            return;
        }

        /* Check if inputPlayer is already in the buffer */
        if (ListFunctions.containsEIC(blacklistedPlayersBuffer, correctSpelling)) {
            String errorMessage = Chat.orange(correctSpelling) + " is already blacklisted!";
            Chat.msgClient(errorMessage, Chat.RED);
            return;
        }

        /* Adds the player to the player buffer */
        blacklistedPlayersBuffer.add(correctSpelling);

        if (StatsDisplay.currentServer.getType().equals("BedWars")) {
            String message = "Temporarily added " + Chat.orange(correctSpelling) + " to your blacklisted players.";
            Chat.msgClient(message, Chat.GREEN);
        }
    }

    /* Check each buffered player if he should be added to the blacklist */
    public static void processBufferedPlayers() {
        for (int i = blacklistedPlayersBuffer.size() - 1; i >= 0; i--) {
            String selectedPlayer = blacklistedPlayersBuffer.get(i);
            blacklistedPlayersBuffer.remove(selectedPlayer);

            /* Step zero: Check of the player is already blacklisted */
            if (blacklistedPlayers.contains(selectedPlayer)) {
                String correctSpelling = blacklistedPlayers.getSpelling(selectedPlayer);
                String errorMessage = "The player " + Chat.orange(correctSpelling) + " is already blacklisted!";
                Chat.msgClient(errorMessage, Chat.RED);
                return;
            }

            /* Step one: Create a MojangPlayer for the selected player */
            MojangPlayer newPlayer = new MojangPlayer(selectedPlayer);

            /* Step two: Check if the buffered player is a real player */
            if (!newPlayer.isValid()) {
                String errorMessage = Chat.orange(selectedPlayer) + " is either misspelled or nicked!";
                Chat.msgClient(errorMessage, Chat.RED);
                return;
            }

            /* Step three: If the player is real, try to add him to the main blacklist */
            boolean successful = blacklistedPlayers.add(newPlayer);
            if (successful) {
                String message = "Successfully added " + Chat.orange(newPlayer.getName()) + " to your blacklisted players.";
                Chat.msgClient(message, Chat.GREEN);

                /* Save changes */
                saveBlacklistedPlayers();
            } else {
                /* In theory shouldn't be activated at all since step zero catches this case */
                String errorCode = "(Error 1: Logic not working)";
                String errorMessage = "Something went wrong! " + Chat.orange(errorCode);
                Chat.msgClient(errorMessage, Chat.RED);
            }
        }
    }

    /* Remove a player from the blacklist */
    /* TODO If the player is in the list, don't get its spelling from Mojang but rather the list itself */
    private void removePlayer(final String inputPlayer) {
        /* Step zero: Get the correct spelling of inputPlayer */
        String correctSpelling = MojangAPI.getSpelling(inputPlayer);

        /* Step one: Check if correctSpelling is null */
        if (correctSpelling == null) {
            String errorMessage = Chat.orange(inputPlayer) + " is not blacklisted!";
            Chat.msgClient(errorMessage, Chat.RED);
            return;
        }

        /* Step two: Try to remove inputPlayer */
        boolean successful = blacklistedPlayers.remove(correctSpelling);
        if (successful) {
            String message = "Successfully removed " + Chat.orange(correctSpelling) + " from your blacklisted players.";
            Chat.msgClient(message, Chat.GREEN);

            /* Save changes */
            saveBlacklistedPlayers();
        } else {
            String errorMessage = Chat.orange(correctSpelling) + " is not blacklisted!";
            Chat.msgClient(errorMessage, Chat.RED);
        }
    }

    /* Add a clan to the blacklist */
    private void addClan(final String inputClan) {
        boolean successful = ListFunctions.addEIC(blacklistedClans, inputClan);
        if (successful) {
            String message = "Successfully added " + Chat.orange(inputClan) + " to your blacklisted clans.";
            Chat.msgClient(message, Chat.GREEN);

            /* Save changes */
            saveBlacklistedClans();
        } else {
            String errorMessage = "The clan " + Chat.orange(inputClan) + " is already blacklisted!";
            Chat.msgClient(errorMessage, Chat.RED);
        }
    }

    /* Update the spelling of a clan */
    public static void updateClanSpelling(String newSpelling) {
        for (int i = blacklistedClans.size() - 1; i >= 0; i--) {
            String currentClan = blacklistedClans.get(i);
            if (!currentClan.equals(newSpelling) && currentClan.equalsIgnoreCase(newSpelling)) {
                blacklistedClans.remove(i);
                blacklistedClans.add(newSpelling);

                /* Notification to see if method is called */
                /* String message = "Updated spelling from " + Chat.purple(currentClan) + " to " + Chat.orange(newSpelling);
                Chat.msgClient(message, Chat.GREEN); */

                /* Save changes */
                saveBlacklistedClans();
            }
        }
    }

    /* Remove a clan from the blacklist */
    private void removeClan(final String inputClan) {
        String correctSpelling = ListFunctions.getSpellingEIC(blacklistedClans, inputClan);
        boolean successful = ListFunctions.removeEIC(blacklistedClans, inputClan);
        if (successful) {
            String message = "Successfully removed " + Chat.orange(correctSpelling) + " from your blacklisted clans.";
            Chat.msgClient(message, Chat.GREEN);

            /* Save changes */
            saveBlacklistedClans();
        } else {
            String errorMessage = Chat.orange(inputClan) + " is not blacklisted!";
            Chat.msgClient(errorMessage, Chat.RED);
        }
    }

    /* Loads the files that store the blacklisted players and clans */
    public static void loadBlacklists() {
        WriteToFile clans = new WriteToFile("clans.txt");
        blacklistedClans = clans.readClans();

        WriteToFile players = new WriteToFile("players.txt");
        blacklistedPlayers = players.readPlayers();
    }

    /* Saves blacklisted clans to drive */
    private static void saveBlacklistedClans() {

        /* Erase all entries */
        WriteToFile clans = new WriteToFile("clans.txt");
        clans.clear();

        /* Writes the elements down */
        clans.writeClans(blacklistedClans);
    }

    /* Saves blacklisted players to drive */
    private static void saveBlacklistedPlayers() {

        /* Erase all entries */
        WriteToFile players = new WriteToFile("players.txt");
        players.clear();

        /* Writes the elements down */
        players.writePlayers(blacklistedPlayers);
    }

    /* Print the blacklisted clans in chat */
    public static void printClans() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < blacklistedClans.size() - 1; i++) {
            stringBuilder.append(blacklistedClans.get(i)).append(", ");
        }
        if (blacklistedClans.size() > 0) {
            stringBuilder.append(blacklistedClans.get(blacklistedClans.size() - 1));
        }
        Chat.msgClient(stringBuilder.toString(), Chat.PURPLE);
    }

    /* Print the blacklisted players in chat */
    public static void printPlayers() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < blacklistedPlayers.playerList.size() - 1; i++) {
            stringBuilder.append(blacklistedPlayers.playerList.get(i).getName()).append(", ");
        }
        if (blacklistedPlayers.playerList.size() > 0) {
            stringBuilder.append(blacklistedPlayers.playerList.get(blacklistedPlayers.playerList.size() - 1).getName());
        }
        Chat.msgClient(stringBuilder.toString(), Chat.PURPLE);
    }

    /* Updates the player names if they were changed */
    public static void updatePlayerNames() {
        WriteToFile fileWriter = new WriteToFile("players.txt");
        ArrayList<String> UUIDs = fileWriter.getSecRow();
        /* A list with all (new) player names */
        ArrayList<String> updatedNames = MojangAPI.UUIDtoPlayer(UUIDs);

        /* Check if a UUID got lost */
        if (UUIDs.size() != updatedNames.size()) {
            /* Failed to update the UUIDs */
            return;
        }

        fileWriter.clear();

        for (int i = 0; i < UUIDs.size(); i++) {
            String UUID = UUIDs.get(i);
            String name = updatedNames.get(i);
            String newEntry =  name + " " + UUID;
            fileWriter.write(newEntry);
        }
    }
}