package statsdisplay.util;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MojangAPI {
    /* Constructor */
    MojangAPI() {

    }

    /* Return the correct spelling of a player. If the player does not exist, return null */
    public static String getSpelling(String userName){
        String sURL = "https://api.mojang.com/users/profiles/minecraft/" + userName;
        try {
            new URL(sURL).openConnection().connect();
            return new JsonParser().parse(new InputStreamReader((InputStream) new URL(sURL).openConnection().getContent())).getAsJsonObject().get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Return the corresponding UUID of a player. If the player does not exist, return null */
    public static String getUUID(String userName){
        String sURL = "https://api.mojang.com/users/profiles/minecraft/" + userName;
        try {
            new URL(sURL).openConnection().connect();
            return new JsonParser().parse(new InputStreamReader((InputStream) new URL(sURL).openConnection().getContent())).getAsJsonObject().get("id").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Return an ArrayList of player names according to their UUIDs */
    public static ArrayList<String> UUIDtoPlayer(ArrayList<String> UUIDs) {
        ArrayList<String> result = new ArrayList<>();
        String prefix = "https://api.mojang.com/user/profiles/";
        String suffix = "/names";
        for (String uuid : UUIDs) {
            String url = prefix + uuid + suffix;
            try {
                new URL(url).openConnection().connect();
                String playerName = new JsonParser().parse(new InputStreamReader((InputStream) new URL(url).openConnection().getContent())).getAsJsonObject().get("name").getAsString();
                result.add(playerName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
