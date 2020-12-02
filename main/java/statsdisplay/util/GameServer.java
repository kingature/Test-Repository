package statsdisplay.util;

public class GameServer {
    private String type, uuid;

    public GameServer() {
        this.type = "Not Found";
        this.uuid = "Not Found";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null) {
            this.type = "NA";
        } else if (type.startsWith("BW")) {
            this.type = "BedWars";
        } else this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
