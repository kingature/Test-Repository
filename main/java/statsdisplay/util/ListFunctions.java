package statsdisplay.util;

import java.util.ArrayList;

public class ListFunctions {

    /* Remove an item form an ArrayList */
    public static boolean removeEIC(ArrayList<String> list, String anotherString) {
        for (int i = list.size() - 1; i >= 0; i--) {
            String item = list.get(i);
            if (item.equalsIgnoreCase(anotherString)) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    /* Add an item to the list */
    public static boolean addEIC(ArrayList<String> list, String anotherString) {
        if (!containsEIC(list, anotherString)) {
            list.add(anotherString);
            return true;
        }
        return false;
    }

    /* Return whether an item is in that list */
    public static boolean containsEIC(ArrayList<String> list, String anotherString) {
        for (String s : list) {
            if (s.equalsIgnoreCase(anotherString)) {
                return true;
            }
        }
        return false;
    }

    /* Return the correct spelling of an element */
    public static String getSpellingEIC(ArrayList<String> list, String anotherString) {
        for (String item : list) {
            if (item.equalsIgnoreCase(anotherString)) {
                return item;
            }
        }
        return "Clan not found!";
    }
}
