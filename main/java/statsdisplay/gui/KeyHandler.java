package statsdisplay.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import statsdisplay.main.StatsDisplay;

import static statsdisplay.main.StatsDisplay.onGomme;

public class KeyHandler {
    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        /* Only have the addon activated when playing on GommeHD */
        if (!onGomme) return;

        /* Check whether the pressed key equals the key for opening the gui */
        if (StatsDisplay.key.isKeyDown()) {
            /* Open the GUI */
            Minecraft.getMinecraft().displayGuiScreen(new StatsGUI());
        }
    }
}
