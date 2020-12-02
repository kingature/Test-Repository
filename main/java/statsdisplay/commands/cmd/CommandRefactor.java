package statsdisplay.commands.cmd;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import statsdisplay.commands.StatsDisplayCommand;
import statsdisplay.main.StatsDisplay;
import statsdisplay.util.Chat;
import statsdisplay.util.ChatBlock;

public class CommandRefactor extends StatsDisplayCommand {
    /* Return command name */
    public String getName() {
        return ".sheep";
    }

    /* Get command usage */
    public String[] getCommandUsage() {
        return new String[]{".sheep"};
    }

    /* Method that is executed when the command is called */
    public void execute(String param) {
        StatsDisplay.sheep = !StatsDisplay.sheep;
        String message = "Sheep timer is now set to " + Chat.orange("" + StatsDisplay.sheep);
        Chat.msgClient(message, Chat.GREEN);

        /* for (ChatBlock block : StatsDisplay.chatBlocks) {
            block.print();
        } */
    }
}