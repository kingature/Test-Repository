package statsdisplay.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import statsdisplay.gui.Events;
import statsdisplay.main.StatsDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PlayerList {
    /* List to store players */
    private final ArrayList<StatsPlayer> players;

    /* Constructor to create a player list */
    public PlayerList() {
        players = new ArrayList<StatsPlayer>();
    }

    /* Adds a player to the list when not already inside */
    public void add(StatsPlayer p) {
        if (!contains(p)) {
            players.add(p);
        }
    }

    /* Adds a player to the list when not already inside */
    public void add(String name) {
        if (!this.contains(name)) {
            StatsPlayer newPlayer = new StatsPlayer(name);
            players.add(newPlayer);
        }
    }

    /* Remove an player from the list */
    public void remove(StatsPlayer p) {
        for (int i = players.size() - 1; i >= 0; i--) {
            StatsPlayer sp = players.get(i);
            if (sp.getName().equalsIgnoreCase(p.getName())) {
                players.remove(i);
            }
        }
    }

    /* Remove a player from the list (acc. to his name) */
    public void remove(String p) {
        for (int i = players.size() - 1; i >= 0; i--) {
            StatsPlayer sp = players.get(i);
            if (sp.getName().equalsIgnoreCase(p)) {
                players.remove(i);
            }
        }
    }

    /* Remove a player from the list (acc. to index) */
    public void remove(int i) {
        players.remove(i);
    }

    /* Return whether a player is in the list */
    public boolean contains(StatsPlayer p) {
        for (StatsPlayer player : players) {
            if (player.getName().equalsIgnoreCase(p.getName())) {
                return true;
            }
        }
        return false;
    }

    /* Return whether a player is in thr list (acc. to his name) */
    public boolean contains(String p) {
        for (StatsPlayer player : players) {
            if (player.getName().equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    /* Shows a list of all players with their properties in chat */
    public void show() {
        Chat.msgClient("", "", false);
        Chat.msgClient("Name, KD, Clan, Team, Party, Spec", "§b");
        for (int i = 0; i < players.size(); i++) {
            StatsPlayer player = players.get(i);
            String message_layout = player.asString();
            if (i % 2 == 0) {
                Chat.msgClient(message_layout, "§3");
            } else {
                Chat.msgClient(message_layout, "§b");
            }
            System.out.println(message_layout);
        }
        Chat.msgClient("", "", false);
    }

    /* Clears the list */
    public void clear() {
        players.clear();
    }

    /* Returns the length of the list */
    public int size() {
        return players.size();
    }

    /* Return a player's stats */
    public float getStats(String name) {
        /* If the requested player is in this list, return its stats */
        if (this.contains(name)) {
            StatsPlayer player = this.get(name);
            return player.getKD();
        }

        /* If the player is not in this list, return some crap */
        return StatsDisplay.WIP;
    }

    /* Return a player */
    public StatsPlayer get(int i) {
        if (i < players.size()) {
            return players.get(i);
        }
        return null;
    }

    /* Return a player */
    public StatsPlayer get(String name) {
        for (int i = 0; i < this.size(); i++) {
            StatsPlayer player = this.get(i);
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    /* Return a player */
    public StatsPlayer get(StatsPlayer player) {
        if (this.contains(player)) {
            return this.get(player);
        }
        return null;
    }

    /* Overrides the player stats in playerList with them from stats */
    public void updateStats(PlayerList list) {
        for (int i = 0; i < list.size(); i++) {
            StatsPlayer player = list.get(i);
            StatsPlayer toUpdate = this.get(player.getName());
            if (toUpdate != null) {
                toUpdate.setKD(player.getKD());
            }
        }
    }

    /* Adds new players, removes offline player */
    public void update() {
        ArrayList<String> referencePlayers = getPlayers();

        /* If there is a player in referencePlayers that is not in this list, add it to this list */
        for (String playerName : referencePlayers) {
            if (!this.contains(playerName)) {
                this.add(playerName);
            }
        }

        /* If there is a player in this list that is not in referencePlayers, remove it from this list */
        for (int i = this.size() - 1; i >= 0; i--) {
            StatsPlayer player = this.get(i);
            if (!referencePlayers.contains(player.getName())) {
                /* this.remove(i); Removed because sometimes player bug out of tab and are getting checked again */
            }
        }

        updatePlayerInfo();
    }

    /* Adds all elements to the player list*/
    private void addAll(ArrayList<StatsPlayer> list) {
        this.players.addAll(list);
    }

    /* Returns a list with all players (names) that are on the same server as you are */
    private ArrayList<String> getPlayers() { // returns a list of all players playing on the server
        ArrayList<String> online_players = new ArrayList<>();

        EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
        NetHandlerPlayClient handler = Minecraft.getMinecraft().getNetHandler();
        if (handler != null) {
            Collection<NetworkPlayerInfo> list = handler.getPlayerInfoMap();
            if (list == null || list.isEmpty()) {
                //sender.addChatMessage(new ChatComponentText("Collection<NetworkPlayerInfo> ist null oder leer!"));
                return online_players;
            }
            Iterator<NetworkPlayerInfo> iter = list.iterator();

            String str = "";
            for (;iter.hasNext();) {
                NetworkPlayerInfo info = iter.next();
                if (iter.hasNext()) {
                    str = str + (info == null ? "null" : info.getGameProfile().getName()) + ", ";
                    online_players.add(info.getGameProfile().getName());
                } else {
                    str = str + (info == null ? "null" : info.getGameProfile().getName());
                    online_players.add(info.getGameProfile().getName());
                }
            }
            if (isBlank(str)) {
                Chat.msgClient("No players found", "§c");
                return online_players;
            }
        } else {
            Chat.msgClient("NetHandlerPlayClient is null", "§c");
        }
        return online_players;
    }
    private boolean isBlank(String str) {
        if (str == null || str.isEmpty())
            return true;

        for (char c : str.toCharArray()) {
            if (c != ' ' && c != '	') {
                return false;
            }
        }
        return true;
    }

    private void updatePlayerInfo() {
        Collection<NetworkPlayerInfo> players =  Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        for (NetworkPlayerInfo info : players) {
            Events.tabList.getPlayerName(info);
        }
    }

    /* Returns the correct spelling of a player on tab */
    public String getSpelling(String targetName) {
        for (StatsPlayer player : players) {
            String playerName = player.getName();
            if (playerName.equalsIgnoreCase(targetName)) {
                return playerName;
            }
        }
        return targetName;
    }
}