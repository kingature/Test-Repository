package statsdisplay.util;

import java.io.*;
import java.util.ArrayList;

public class WriteToFile {
    private final String name;

    /* Constructor */
    public WriteToFile(String name) {
        this.name = name;
    }

    /* Creates a file */
    public void create() {
        File file = new File(name);
        try {
            if (file.createNewFile()) {
                Chat.msgClient("Created new file", Chat.GREEN);
            } else Chat.msgClient("File already exists", Chat.RED);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /* Writes the input into a new line */
    public void write(String input) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(name, true));
            bufferedWriter.append(input).append("\n");
            bufferedWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /* Writes the input into a new line */
    public void writeClans(ArrayList<String> input) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(name, true));
            for (String clan : input) {
                bufferedWriter.append(clan).append("\n");
            }
            bufferedWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /* Writes the input into a new line */
    public void writePlayers(MojangPlayerList input) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(name, true));
            for (MojangPlayer player : input.playerList) {
                bufferedWriter.append(player.getName()).append(" ").append(player.getUUID()).append("\n");
            }
            bufferedWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /* Returns an ArrayList of strings where each line is one element */
    public ArrayList<String> readClans() {
        ArrayList<String> result = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(name));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.equals("")) {
                    result.add(line.trim());
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /* Reads the maxKD value */
    public String readKD() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(name));
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            Chat.msgClient("File not found", Chat.RED);
        }
        return "10";
    }

    /* Returns a MojangPlayerList of all players */
    public MojangPlayerList readPlayers() {
        MojangPlayerList result = new MojangPlayerList();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(name));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.equals("")) {
                    String[] parts = line.split("\\s+");
                    /* Format: Name, UUID */
                    MojangPlayer newPlayer = new MojangPlayer(parts[0], parts[1]);
                    result.add(newPlayer);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /* Deletes all content of a file */
    public void clear() {
        try {
            FileWriter fileWriter = new FileWriter(name);
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    /* Returns the UUIDs of the player.txt file */
    public ArrayList<String> getSecRow() {
        ArrayList<String> result = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(name));
            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.equals("")) {
                    String[] parts = line.split("\\s+");
                    /* Format: Name, UUID */
                    String uuid = parts[1];
                    result.add(uuid);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
