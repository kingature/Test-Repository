package statsdisplay.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import statsdisplay.main.NewTabList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Events {
    /* The instance of the in game GUI, which got its tab list instance replaced */
    private GuiIngame lastIngameGUI = null;

    public static NewTabList tabList;

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
        /* Check whether the StatsGUI was opened */
        if (event.gui instanceof StatsGUI) {

            /* Set the text field value to the current max. KD for AutoLeave */
            StatsGUI.textField_maxKD_value = StatsGUI.maxKD;
        }

        /* Responsible for modifying the tab list */
        Minecraft mc = Minecraft.getMinecraft();

        if (event.gui == null) return;

        if (mc.ingameGUI == lastIngameGUI) return;
        lastIngameGUI = mc.ingameGUI;
        try {
            Class<?> clazz = lastIngameGUI.getClass();
            Field tabField = null;
            for (Field field : clazz.getDeclaredFields()) {
                if (GuiPlayerTabOverlay.class.isAssignableFrom(field.getType())) {
                    tabField = field;
                    break;
                }
            }
            tabField.setAccessible(true);
            Field modField = Field.class.getDeclaredField("modifiers");
            modField.setAccessible(true);
            modField.setInt(tabField, tabField.getModifiers() & ~Modifier.FINAL);
            tabList = new NewTabList(mc, lastIngameGUI);
            tabField.set(lastIngameGUI, tabList);
        } catch (Throwable t) {
            System.err.println("Failed to replace tab list!");
            t.printStackTrace();
        }
    }
}
