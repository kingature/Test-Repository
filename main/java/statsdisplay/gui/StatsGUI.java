package statsdisplay.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.math.NumberUtils;
import statsdisplay.util.Chat;
import statsdisplay.main.StatsDisplay;
import statsdisplay.util.WriteToFile;

import java.io.IOException;

/* text height = 8 Minecraft pixels
 * button height = 20 Minecraft pixels
 */

public class StatsGUI extends GuiScreen {
    /* Button to  en- or disable the stats on tab */
    public static String button_enabled_value = "ON";
    public GuiButton button_enabled;

    /* Button to  en- or disable AutoLeave */
    public static String button_autoLeave_value = "ON";
    public GuiButton button_autoLeave;

    /* Field to set the max. KD */
    public static String textField_maxKD_value = loadMaxKD();
    public GuiTextField textField_maxKD;

    /* Button to confirm the KD */
    public static String button_confirmKD_value = "OK";
    public GuiButton button_confirmKD;

    /* Value that holds the max. KD for AutoLeave */
    public static String maxKD = textField_maxKD_value;

    @Override
    public void initGui() {
        /* Initialize each button */
        buttonList.add(button_enabled = new GuiButton(0, width / 3, 66,  40,20, button_enabled_value));
        buttonList.add(button_autoLeave = new GuiButton(1, width / 3, 91, 40,20, button_autoLeave_value));
        buttonList.add(button_confirmKD = new GuiButton(3, width / 3 + 45, 116, 40,20, button_confirmKD_value));

        /* Set the properties for the text field */
        textField_maxKD = new GuiTextField(2, fontRendererObj, width / 3, 116, 40, 20);
        textField_maxKD.setMaxStringLength(4);
        textField_maxKD.setFocused(true);
        textField_maxKD.setCanLoseFocus(true);
    }

    @Override
    /* Updates the cursor of the text field */
    public void updateScreen() {
        textField_maxKD.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    /* Is activated when a key is pressed inside the text field */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        /* Only accept digits or delete */
        if (NumberUtils.isNumber(Character.toString(typedChar)) || keyCode == 14) {
            textField_maxKD.textboxKeyTyped(typedChar, keyCode);
        }

        /* Update the value of the text field that will be shown in next frame */
        textField_maxKD_value = textField_maxKD.getText();

        /* Deactivate the confirm button when the text field is empty or contains only zeros */
        button_confirmKD.enabled = !textField_maxKD_value.equals("") && (!NumberUtils.isNumber(textField_maxKD_value) || (int) Float.parseFloat(textField_maxKD_value) != 0);

        /* Call the super method */
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    /* Responsible for drawing all the elements on the screen */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        /* Draw a translucent background for the GUI */
        drawDefaultBackground();

        /* Draws the headline of the GUI */
        String headline = "StatsDisplay Menu";
        int l = fontRendererObj.getStringWidth(headline);
        fontRendererObj.drawString(headline, (width - l) / 2, 20, 0xFFFFFFFF);

        /* Draws the descriptions for the buttons */
        fontRendererObj.drawString("Show Stats", 20, 72, 0xFFFFFFFF);
        fontRendererObj.drawString("Auto Leave", 20, 97, 0xFFFFFFFF);
        fontRendererObj.drawString("Max. KD", 20, 122, 0xFFFFFFFF);

        /* Draw the text field with its content */
        textField_maxKD.drawTextBox();
        textField_maxKD.setText(textField_maxKD_value);

        /* Call the super method */
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    /* Check which button was pressed and execute the corresponding action */
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0 :
                /* Change the button text according to its state */
                if (button_enabled_value.equalsIgnoreCase("ON")) {
                    button_enabled_value = "OFF";
                    StatsDisplay.enabled = false;
                } else {
                    button_enabled_value = "ON";
                    StatsDisplay.enabled = true;
                }

                /* Clear the button list in order to update the button text */
                buttonList.clear();

                /* Add all buttons with their new values */
                initGui();
                break;
            case 1 :
                /* Change the button text according to its state */
                if (button_autoLeave_value.equalsIgnoreCase("ON")) {
                    button_autoLeave_value = "OFF";
                    StatsDisplay.autoLeave = false;
                } else {
                    button_autoLeave_value = "ON";
                    StatsDisplay.autoLeave = true;
                }
                /* Clear the button list in order to update the button text */
                buttonList.clear();

                /* Add all buttons with their new values */
                initGui();
                break;
            case 3 :
                /* Confirm the value in the text field as the value for AutoLeave */
                maxKD = textField_maxKD_value;

                /* Remove all the front zeros in maxKD */
                while (maxKD.startsWith("0")) {
                   maxKD = maxKD.substring(1);
                }

                /* If maxKD is (for some reason) not a number, make it to a number */
                if (!NumberUtils.isNumber(maxKD)) {
                    /* Set maxKD to 10, since the value entered before wasn't a number */
                    maxKD = "10";

                    /* Print error message in chat */
                    String errorMessage = "Ups, something went wrong! You entered an invalid value! Your max. KD was set to default (10) again";
                    Chat.msgClient(errorMessage, Chat.RED);
                    return;
                }

                /* Send a confirmation message to the user */
                Chat.msgClient("You will now leave at a KD greater than " + Chat.ORANGE + maxKD + Chat.GREEN + ".", Chat.GREEN);

                /* Write maxKD to file */
                saveMaxKD(maxKD);
                break;
        }
    }

    /* Reads the maxKD from text document */
    public static String loadMaxKD() {
        WriteToFile maxKD_Reader = new WriteToFile("maxKD.txt");
        String output = maxKD_Reader.readKD();
        if (NumberUtils.isNumber(output)) {
            return output;
        } else return "10";
    }

    /* Saves the KD to file */
    private void saveMaxKD(String KD) {
        WriteToFile maxKD_Reader = new WriteToFile("maxKD.txt");
        maxKD_Reader.clear();
        maxKD_Reader.write(KD);
    }
}