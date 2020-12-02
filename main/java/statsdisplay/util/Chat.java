package statsdisplay.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Chat {
    /* Standard colors */
    public final static String RED = "§c§c", GREEN = "§a" , PURPLE = "§d", ORANGE = "§6";

    /* Message or command which will be send to the server */
    public static void msgServer(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }

    /* Message will be seen by the client only */
    public static void msgClient(String message, String color) {
        String PREFIX = "§a[§6Stats§eDisplay§a] ";
        try {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(PREFIX + fullColored(message, color)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Message will be seen by the client only (without the StatsDisplay logo) */
    public static void msgClient(String message, String color, boolean watermark) {
        if (watermark) {
            msgClient(message, color);
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(fullColored(message, color)));
        }
    }

    /* Adds the color in front of each word so that line breaks are no problem */
    private static String fullColored(String msg, String col) {
        //return msg==null ? null :Arrays.stream(msg.split("\\s+")).map(s -> col + s + " ").collect(Collectors.joining()).substring(0, Arrays.stream(msg.split("\\s+")).map(s -> col + s + " ").collect(Collectors.joining()).length() - 1);
        if (msg == null) return "";

        String[] parts = msg.split("\\s+");
        StringBuilder result  = new StringBuilder();
        for (String s : parts) {
            result.append(col).append(s).append(" ");
        }
        return result.toString().trim();
    }

    /* Methods that enclose a string with color */
    public static String orange(String input) {
        String[] parts = input.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String s : parts) {
            result.append(ORANGE).append(s).append(" ");
        }
        return result.toString().trim() + "§r";
    }
    public static String purple(String input) {
        String[] parts = input.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String s : parts) {
            result.append(PURPLE).append(s).append(" ");
        }
        return result.toString().trim() + "§r";
    }
}