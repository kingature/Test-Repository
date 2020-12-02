package statsdisplay.util;

import java.util.ArrayList;

public class MojangPlayerList {
    /* ArrayList which holds all the players */
    public ArrayList<MojangPlayer> playerList = new ArrayList<>();

    /* Constructor */
    public MojangPlayerList() {

    }

    /* Removes an element, not key sensitive */
    public boolean remove(String name) {
        for (int i = playerList.size() - 1; i >= 0; i--) {
            if (playerList.get(i).getName().equalsIgnoreCase(name)) {
                playerList.remove(i);
                return true;
            }
        }
        return false;
    }

    /* Add a player */
    public boolean add(MojangPlayer anotherPlayer) {
        if (this.contains(anotherPlayer)) {
            return false;
        } else {
            this.playerList.add(anotherPlayer);
            return true;
        }
    }

    /* Return if MojangPlayer is already in the list */
    private boolean contains(MojangPlayer anotherPlayer) {
        for (MojangPlayer player : this.playerList) {
            if (player.getUUID().equalsIgnoreCase(anotherPlayer.getUUID())) {
                return true;
            }
        }
        return false;
    }
    public boolean contains(String anotherPlayer) {
        for (MojangPlayer player : playerList) {
            if (anotherPlayer.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    /* Return the string of an element */
    public String getSpelling(String inputPlayer) {
        for (MojangPlayer player : playerList) {
            String playerName = player.getName();
            if (playerName.equalsIgnoreCase(inputPlayer)) {
                return playerName;
            }
        }
        return "Player not found!";
    }
}