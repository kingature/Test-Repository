package statsdisplay.util;

import statsdisplay.main.StatsDisplay;

public class StatsPlayer {

    /* Default value for a player's clan and team */
    public static String DEFAULT = "default";

    /* Attributes of a StatsPlayer */
    private String name, clan = DEFAULT, team = DEFAULT;
    private boolean party = false, checked = false;
    private float KD = StatsDisplay.PROCESSING;
    public boolean requestedByPlayer;

    /* Constructor for StatsPlayer */
    public StatsPlayer(String name, float KD) {
        this.name = name;
        this.KD = KD;
    }

    /* Constructor for StatsPlayer */
    public StatsPlayer(String name) {
        this.name = name;
    }

    /* Method to check the stats of this player */
    public void checkStats() {
        Chat.msgServer("/stats " + name);
    }

    /* Get and set a player's name */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /* Get and set a player's KD */
    public float getKD() {
        return KD;
    }
    public void setKD(float KD) {
        this.KD = KD;
    }

    /* Get and set a player's clan */
    public String getClan() {
        return clan;
    }
    public void setClan(String clan) {
        this.clan = clan;
    }

    /* Get and set whether a player is in your party */
    public boolean isParty() {
        return party;
    }
    public void setParty(boolean party) {
        this.party = party;
    }

    /* Get and set a player's team */
    public String getTeam() {
        return team;
    }
    public void setTeam(String team) {
        this.team = team;
    }

    /* Get and set whether a player's statistics were checked */
    public boolean isChecked() {return this.checked;}
    public void setChecked(boolean checked) {this.checked = checked;}

    /* Returns the player as a String */
    public String asString() {
        return "Name: " + name + ", KD: " + KD + ", Clan: " + clan + ", Team: " + team + ", Party: " + party;
    }
}
