package statsdisplay.util;

public class MojangPlayer {
    private final String UUID;
    private String name;

    /* Constructor */
    public MojangPlayer(String name) {
        this.name = MojangAPI.getSpelling(name);
        this.UUID = MojangAPI.getUUID(this.name);
    }

    /* Constructor */
    public MojangPlayer(String name, String uuid) {
        this.name = name;
        this.UUID = uuid;
    }

    /* Return the UUID */
    public String getUUID() {
        return UUID;
    }

    /* Get and set a player's name */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /* Return if a player is valid */
    public boolean isValid() {
        return this.name != null && this.UUID != null;
    }
}