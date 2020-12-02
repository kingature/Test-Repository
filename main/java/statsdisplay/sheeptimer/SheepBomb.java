package statsdisplay.sheeptimer;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;
import statsdisplay.util.Chat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class SheepBomb {
    private final EntitySheep sheep;
    private final World world;
    private final EntityArmorStand timeDisplay;
    private float age;
    private final long birth;
    private final String woolColor;
    private boolean dead = false;

    /* Constructor */
    public SheepBomb(EntitySheep sheep, World world) {
        /* Assign the values from constructor */
        this.sheep = sheep;
        this.world = world;

        /* Extract further information */
        this.birth = world.getTotalWorldTime();
        this.woolColor = this.sheep.getFleeceColor().getName();

        /* Prepare the armor stand */
        this.timeDisplay = new EntityArmorStand(this.world);
        this.timeDisplay.setInvisible(true);
        this.timeDisplay.setAlwaysRenderNameTag(true);
        this.world.spawnEntityInWorld(this.timeDisplay);
    }

    /* Updates the age of the sheep according to the world time */
    private void updateAge() {
        long currentWorldTime = world.getTotalWorldTime();
        this.age = (float) (currentWorldTime - this.birth) / (float) 20;
    }

    /* Updates text and position of the armor stand */
    private void updateArmorStand() {
        /* Position */
        double x = sheep.posX, y = sheep.posY, z = sheep.posZ;
        this.timeDisplay.setPosition(x, y, z);

        /* Name */
        String displayName = Chat.RED + this.age + " sec.";
        this.timeDisplay.setCustomNameTag(displayName);

        /* Removes the armor stand from the world */
        if (sheep.isDead) {
            this.world.removeEntity(this.timeDisplay);
            dead = true;
            DecimalFormat df = new DecimalFormat("00.00", new DecimalFormatSymbols(Locale.ENGLISH));
            Chat.msgClient("Sheep took " + Chat.orange(df.format(age)) + " seconds to explode.", Chat.GREEN);
        }
    }

    /* Returns the instance of this sheep */
    public EntitySheep getSheep() {
        return this.sheep;
    }

    /* Return if this sheep is dead */
    public boolean isDead() {
        return this.dead;
    }

    /* Does everything to render the armor stand */
    public void render() {
        this.updateAge();
        this.updateArmorStand();
    }
}
