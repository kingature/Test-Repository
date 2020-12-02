package statsdisplay.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.*;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import statsdisplay.commands.CommandHandler;
import statsdisplay.commands.cmd.*;
import statsdisplay.gui.Events;
import statsdisplay.gui.KeyHandler;
import statsdisplay.gui.StatsGUI;
import statsdisplay.sheeptimer.SheepBomb;
import statsdisplay.util.*;

import java.util.*;

/*
   TODO No. 1: Chat should only be filtered when Player not already checked
   TODO No. 2: Switch between all time and 30 days stats
   TODO No. 3: Sort the player list for priority (kinda done, but useless since you leave the game anyway if not sufficient time available)
   TODO No. 5: Update player names when starting the game
   TODO No. 6: Use Interface for commands
   TODO No. 8: When adding a clan, check its spelling first from tab
   TODO No. 9: Add customizable commands shortcuts
 */

public class StatsDisplay extends LabyModAddon {
    /* Boolean to activate the addon if playing on GommeHD.net */
    public static boolean onGomme = false;

    /* Key Bindings */
    public static KeyBinding key = new KeyBinding("Taste zum öffnen der GUI", Keyboard.KEY_U, "Stats Display");

    /* States of a KD */
    public static final float HIDDEN = -1, PROCESSING = -2, WIP = -3;

    /* ArrayList to save the name and the statistics of a player when looking up his statistics */
    private final ArrayList<String> chatReader = new ArrayList<>();

    /* Counter to determine the necessary delay between checking the stats of two players */
    private int tickCount = 0;

    /* When true, the stats of all player will be checked */
    public static boolean enabled = true;

    /* When true, you will leave the game if someone exceeds your max. set KD */
    public static boolean autoLeave = true;

    /* Save information about your last and current server */
    public final static GameServer currentServer = new GameServer();
    private final GameServer previousServer = new GameServer();

    /* List where all the player on a server a stored */
    public final static PlayerList playerList = new PlayerList();

    /* Class which will process all the client side commands */
    private final CommandHandler cmh = new CommandHandler();

    /* Whether you left a server recently */
    private boolean recentL = false;

    /* Whether you'r in a game of BedWars or not */
    private boolean ingameBedWars = false;

    /* SheepTimer which holds all the loaded sheep */
    ArrayList<SheepBomb> sheepBombs = new ArrayList<>();

    /* If true, the sheep timer is activated */
    public static boolean sheep = false;

    /* Long which stores the system's current time */
    long startTime = System.currentTimeMillis();

    /* Delay between checking the stats */
    final long TIME_DIFFERENCE = 1000;

    /* ArrayList to save the buffered chat */
    private static final ArrayList<String> chatBuffer = new ArrayList<>();

    public static final ArrayList<ChatBlock> chatBlocks = new ArrayList<>();

    @Override
    /* Called when starting the game */
    public void onEnable() {
        /* Method where all the custom commands are registered */
        registerCommands();

        /* Registers the onTick listener */
        this.getApi().registerForgeListener(this);

        /* Registers the Forge Events */
        MinecraftForge.EVENT_BUS.register(new Events());

        /* Triggered when joining a server */
        getApi().getEventManager().registerOnJoin(new Consumer<ServerData>() {
            public void accept(ServerData serverData) {
                /* String to store the current server IP */
                String ip = serverData.getIp();

                /* If your are on GommeHD.net, activate the addon */
                String targetIP = "gommehd.net";
                if (ip.equals(targetIP)) {
                    onGomme = true;

                    String welcomeMessage = "You are currently playing with §6Stats§eDisplay" + Chat.GREEN + "! In order to open the main menu press " +
                                            Chat.orange(Keyboard.getKeyName(StatsDisplay.key.getKeyCode())) +
                                            Chat.GREEN + ". Type " + Chat.orange(".help") + " to see all available commands.";

                    /* Show the welcomeMessage in chat */
                    Chat.msgClient(welcomeMessage, Chat.GREEN);
                } else {
                    String errorMessage = "StatsDisplay is currently disabled since its designed for GommeHD.net only!";
                    Chat.msgClient(errorMessage, Chat.RED);
                }
            }
        });

        /* Triggers when disconnecting from a server */
        getApi().getEventManager().registerOnQuit(new Consumer<ServerData>() {
            @Override
            public void accept(ServerData serverData) {
                /* Set onGomme to false since you're in your main menu */
                onGomme = false;
            }
        });

        /* Triggered when sending a message */
        getApi().getEventManager().register(new MessageSendEvent() {
            @Override
            public boolean onSend(String s) {
                /* Only have the addon activated when playing on GommeHD */
                if (!onGomme) return false;

                /* Hand the message first to the CommandHandler in order to filter client only commands */
                return cmh.checkForCommands(s);
            }
        });

        /* Triggered when receiving a message */
        getApi().getEventManager().register(new MessageReceiveEvent() {
            @Override
            public boolean onReceive(String s, String s1) {
                /* Only have the addon activated when playing on GommeHD */
                if (!onGomme) return false;

                /* Triggered when playing on a BedWars server and having the addon activated */
                if (currentServer.getType().equals("BedWars") && enabled) {
                    String receivedMSG = removeColorCode(s1).trim();

                    /* If a received message has important information (Name, KD), save it */
                    String name, KD;
                    if (receivedMSG.startsWith("-= Statistiken von")) {
                        name = receivedMSG.split("\\s+")[3];
                        chatReader.add(name);
                    }
                    if (receivedMSG.startsWith("K/D:")) {
                        KD = receivedMSG.split("\\s+")[1];
                        chatReader.add(KD);
                    }

                    /* Add the message to the chat buffer. (If the messages really should be added
                    *  is getting checked in the method itself) */
                    addToChatBuffer(s, receivedMSG);

                    /* If a received message holds a specific content, don't show it in the chat */
                    return isBlocked(receivedMSG);
                }
                return false; /* false -> message will show up; true -> message won't show up */
            }
        });

        /* Triggered when receiving a server package */
        getApi().getEventManager().register(new ServerMessageEvent() {

            /* Handel incoming server packages */
            public void onServerMessage(String messageKey, JsonElement serverMessage) {
                /* Only have the addon activated when playing on GommeHD */
                if (!onGomme) return;

                /* If the package is an discord_rpc - package, store it in a variable */
                if(messageKey.equals("discord_rpc") && serverMessage.isJsonObject()) {

                    /* JsonObject that stores the received package */
                    JsonObject data = serverMessage.getAsJsonObject();

                    /* Gets the information (game mode, uuid) of the current and updates the of the last game server */
                    if(data.has("game_mode")) {
                        String game_mode = data.get("game_mode").getAsString();
                        if (!game_mode.equals(currentServer.getType())) {
                            previousServer.setType(currentServer.getType());
                            currentServer.setType(game_mode);
                        }
                    }

                    /* UUID, is only called one time exactly when connecting to a new game server */
                    if(data.has("matchSecret")) {
                        String matchSecret = data.get("matchSecret").getAsString();
                        if (!matchSecret.equals(currentServer.getUuid())) {
                            previousServer.setUuid(currentServer.getUuid());
                            currentServer.setUuid(matchSecret);

                            /* Execute everything necessary when changing the game server */
                            changeServer();
                        }
                    }
                }
            }
        } );

        /* Register the hotkeys (GUI only) */
        registerHotkeys();
    }

    @Override
    /* Loads the config before you can play the game */
    public void loadConfig() {
        /* Move the blacklisted items in memory */
        CommandBlacklist.loadBlacklists();

        /* Updates the player names if they got changed */
        /* CommandBlacklist.updatePlayerNames(); */
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
    }

    @SubscribeEvent
    /* Triggered 60 times per second */
    void onTick(TickEvent.ClientTickEvent event) {
        /* Only have the addon activated when playing on GommeHD */
        if (!onGomme) return;

        /* Method which cares about stuff related to the chat buffer (e.g. printing) */
        // handleChatBuffer();

        /* If true, sheep will have a tag which displays their fuse time */
        /* TODO: Sheep - Tag should get processed all time but only rendered when activated. */
        if (sheep) renderSheepBombs();

        /* Increase the tickCounter by one to determine whether a new player's stats can be checked without getting
         * kicked for spam */

        /* Adds new players, removes players that are not online anymore */
        playerList.update();

        /* Long which holds the current time */
        long currentTime = System.currentTimeMillis();


        /* Execute if you're on a BedWars server and statsChecking is enabled and a delay is over */
        if (currentTime >= startTime + TIME_DIFFERENCE && enabled && currentServer.getType().equals("BedWars")) {
            /* Update the time */
            startTime = currentTime;

            tickCount++;
            Chat.msgClient(Integer.toString(tickCount), Chat.GREEN);

            /* Set recentL to false, since you just waited ~30 client ticks */
            recentL = false;

            /* If there is a player who's stats weren't checked yet, check them (but only the first, in order to avoid spam) */
            for (int i = 0; i < playerList.size(); i++) {
                StatsPlayer player = playerList.get(i);
                if (!player.isChecked()) {
                    player.checkStats();
                    player.setChecked(true);
                    player.setKD(HIDDEN);
                    break;
                }
            }

            /* Converts a list of strings (Format: Name, KD) into a list of players with their according properties */
            PlayerList extractedStats = extractStats(chatReader);

            /* Overrides the player stats in playerList with them from stats */
            playerList.updateStats(extractedStats);
        }

        /* Method that handles AutoLeave and the checking if a player is in a game or not */
        if (currentServer.getType().equals("BedWars")) {
            /* Check if you should leave if AutoLeave is enabled and you'r not playing BedWars */
            if (!ingameBedWars && autoLeave && !recentL &&!isSpectator()) {
                checkForBlacklistedClans();
                checkForBlacklistedPlayers();
                checkForMaxKD();
            }

            /* Detect whether a player is in a game according to his experience */
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                if (player.experienceLevel == 1 && currentServer.getType().equals("BedWars") && !isSpectator() && !ingameBedWars) {
                    ingameBedWars = true;
                }
            }
        /* If your not on a BedWars server, check for correct player spelling */
        } else {
            CommandBlacklist.processBufferedPlayers();
        }
    }

    /* Method which does all the sheep rendering */
    private void renderSheepBombs() {
        /* Get a list of all loaded entities */
        List<Entity> worldEntityList = Minecraft.getMinecraft().theWorld.loadedEntityList;
        Object[] worldEntityArray = worldEntityList.toArray();

        /* Add new sheep */
        for (Object entity : worldEntityArray) {

            if (entity instanceof EntitySheep) {
                /* Check if the entity is already in the list */
                boolean contains = false;
                for (SheepBomb bomb : sheepBombs) {
                    if (entity.equals(bomb.getSheep())) {
                        contains = true;
                        break;
                    }
                }

                /* If it is not, add it to the list */
                if (!contains) {
                    World world = Minecraft.getMinecraft().theWorld;
                    SheepBomb newSheepBomb = new SheepBomb((EntitySheep) entity, world);
                    sheepBombs.add(newSheepBomb);
                }
            }

        }

        /* Remove dead sheep from the list */
        for (int i = sheepBombs.size() - 1; i >= 0; i--) {
            if (sheepBombs.get(i).isDead()) {
                sheepBombs.remove(i);
            }
        }

        /* Render the sheep */
        for (SheepBomb bomb : sheepBombs) {
            bomb.render();
        }
    }

    /* Removes the color (and format) of a minecraft string */
    public static String removeColorCode(String msg) {
        if (msg != null) {
            while (msg.contains("§")) {
                int index = msg.indexOf('§');
                msg = new StringBuilder(msg).delete(index, index + 2).toString();
            }
        }
        return msg;
    }

    /* Filter information (Name, KD) of a player from the chat */
    private PlayerList extractStats(ArrayList<String> statistics) {
        PlayerList result = new PlayerList();
        if (statistics.size() % 2 == 0) {
            for (int i = 0; i < statistics.size(); i += 2) {
                String name = statistics.get(i);
                float kd = Float.parseFloat(statistics.get(i + 1));
                result.add(new StatsPlayer(name, kd));
            }
        }

        /* Clears the chatReader, since the previous messages were already processed */
        statistics.clear();
        return result;
    }

    /* Return whether a message should show up in chat or not */
    boolean isBlocked(String message) {
        ArrayList<String> blockedMessagesStart = new ArrayList<>();
        blockedMessagesStart.add("-= Statistiken von");
        blockedMessagesStart.add("Position im Ranking:");
        blockedMessagesStart.add("Kills:");
        blockedMessagesStart.add("Deaths:");
        blockedMessagesStart.add("K/D:");
        blockedMessagesStart.add("Gespielte Spiele:");
        blockedMessagesStart.add("Gewonnene Spiele:");
        blockedMessagesStart.add("Siegeswahrscheinlichkeit:");
        blockedMessagesStart.add("Zerstörte Betten:");
        blockedMessagesStart.add("----------------------");

        for (String content : blockedMessagesStart) {
            if (message.startsWith(content)) {
                return true;
            }
        }
        /* ---------------------------------------------------------------------------------------------------------- */
        ArrayList<String> blockedMessagesContains = new ArrayList<>();
        blockedMessagesContains.add("Die Statistiken von diesem Spieler sind versteckt");

        for (String content : blockedMessagesContains) {
            if (message.contains(content)) {
                return true;
            }
        }
        return false;
    }

    /* Collections of stuff that should be executed when changing the server */
    private void changeServer() {

        /* clears player stats + saved chat when changing the server
        - must be true, since discord_rpc is only send when doing so)
        - called here, since you will receive this package only as you're joining a new server */

        /* Reset all collected data, when changing the server */
        chatReader.clear();
        playerList.clear();

        /* Set to false since you changed the server */
        ingameBedWars = false;

        /* Set to false, since you were successfully teleported to another server */
        recentL = false;

        /* Clear the chatBuffer and chatBlocks since you don't need to see stats of old games */
        chatBuffer.clear();
        chatBlocks.clear();
    }

    /* Register custom client only commands */
    private void registerCommands() {
        cmh.registerCommand(new CommandHelp());
        cmh.registerCommand((new CommandShow()));
        cmh.registerCommand(new CommandBlacklist());
        cmh.registerCommand(new CommandSpell());
        cmh.registerCommand(new CommandRefactor());
    }

    /* Register custom hotkeys (e.g. for opening a GUI) */
    private void registerHotkeys() {
        ClientRegistry.registerKeyBinding(key);
        FMLCommonHandler.instance().bus().register(new KeyHandler());
    }

    /* Leaves the game */
    private void leave() {
        if (!recentL) {
            Chat.msgServer("/l");
            recentL = true;
        }
    }

    /* Check if there is a player with a blacklisted clan */
    private void checkForBlacklistedClans() {
        /* Name of the person playing Minecraft */
        String thisUser = LabyMod.getInstance().getGameProfile().getName();

        for (int i = 0; i < playerList.size(); i++) {
            StatsPlayer player = playerList.get(i);
            String playerName = player.getName();
            String playerClan = player.getClan();

            /* Skip if the player is you (Done, so when you blacklist your own clan, you won't leave because of you) */
            if (thisUser.equalsIgnoreCase(playerName)) {
                continue;
            }

            /* If there is a blacklisted clan in your game, leave */
            if (ListFunctions.containsEIC(CommandBlacklist.blacklistedClans, playerClan)) {
                leave();
                Chat.msgClient("Left because of " + Chat.orange(playerClan) + " ["  + Chat.purple("Reason: Blacklisted Clan" + Chat.RED + "]"), Chat.RED);

                /* Updates the spelling of the saved clan */
                CommandBlacklist.updateClanSpelling(playerClan);
            }
        }
    }

    /* Check if there is a blacklisted player */
    private void checkForBlacklistedPlayers() {
        for (int i = 0; i < playerList.size(); i++) {
            /* Get a player of your global list to see if he is a blacklisted player */
            StatsPlayer player = playerList.get(i);
            String playerName = player.getName();

            /* Don't leave if the player is in a party (must be in yours since you wouldn't be able to see it otherwise */
            if (player.isParty()) {
                continue;
            }

            /* If the selected player is blacklisted or a buffered player, leave the game */
            if (CommandBlacklist.blacklistedPlayers.contains(playerName) || ListFunctions.containsEIC(CommandBlacklist.blacklistedPlayersBuffer, playerName)) {
                leave();
                Chat.msgClient("Left because of " + Chat.orange(playerName) + " ["  + Chat.purple("Reason: Blacklisted Player") + Chat.RED + "]", Chat.RED);
            }
        }
    }

    /* Check if a player exceeds your max. KD */
    private void checkForMaxKD() {
        /* Name of the person playing Minecraft */
        String thisUser = LabyMod.getInstance().getGameProfile().getName();

        for (int i = 0; i < playerList.size(); i++) {

            /* Currently selected player */
            StatsPlayer player = playerList.get(i);

            /* Skip if the player is you */
            if (thisUser.equalsIgnoreCase(player.getName())) {
                continue;
            }

            /* Max. KD */
            float maxStats = Float.parseFloat(StatsGUI.maxKD);

            /* KD of a player */
            float playerStats = player.getKD();

            /* If the checked KD exceeds the max. KD, leave the game */
            if (playerStats >= maxStats) {
                leave();
                Chat.msgClient("Left because of " + Chat.orange(player.getName()) + " [" + Chat.purple("Reason: " + playerStats + " KD") + Chat.RED + "]", Chat.RED);
            }
        }
    }

    /* Whether the player is in spectator mode or not */
    private boolean isSpectator() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Collection<PotionEffect> potion = player.getActivePotionEffects();
        for (PotionEffect effect : potion) {
            String name = effect.getEffectName();
            int duration = effect.getDuration();

            if (name.equalsIgnoreCase("potion.invisibility") && duration >= 1000) {
                return true;
            }
        }
        return false;
    }

    /* Step one: Check if the message should be added to the chat buffer */
    private void addToChatBuffer(String original, String clean) {
        /* Blacklisted phrases */
        List<String> blockedMessagesStart = Arrays.asList("-= Statistiken von", "Position im Ranking:", "Kills:",
        "Deaths:", "K/D:", "Gespielte Spiele:", "Gewonnene Spiele:", "Siegeswahrscheinlichkeit:", "Zerstörte Betten:",
        "----------------------");

        /* Iterate through every element in blockedMessagesStart to see if the incoming message starts with it */
        for (String blacklistedMessage : blockedMessagesStart) {
            if (clean.startsWith(blacklistedMessage)) {
                /* If so, add the blocked message to the chat buffer so it can be reused later */
                chatBuffer.add(original);
            }
        }
    }

    /* Extract, delete and print the chat buffer */
    private void handleChatBuffer() {
        /* ---------------------------------------------------------------------------------------------------------- */
        /* TODO: The round stats have a different format -> Check the one before to see if it is an ending message */
        /* Current size of the chatBuffer */
        int chatBufferSize = chatBuffer.size();

        /* If there is a multiple of ten elements in the list (a full ChatBlock), convert the chatBuffer into a ChatBlock */
        if (chatBufferSize != 0 && chatBufferSize % 10 == 0) {

            /* ArrayList of the first ten elements */
            List<String> subList = chatBuffer.subList(0, 10);

            /* Get the player name of the new ChatBlock */
            String playerName = chatBuffer.get(0).split("\\s+")[3];

            /* Initialize the new ChatBlock */
            ChatBlock newBlock = new ChatBlock(playerName);

            /* Go through the first ten elements of the chatBuffer and add them to the ChatBlock */
            for (String part : subList) {
                newBlock.add(part);
            }

            /* Print the ChatBlock in chat */
            /* newBlock.print(); */

            /* Add the new ChatBlock to the list */
            boolean alreadyAdded = false;
            for (ChatBlock block : chatBlocks) {
                if (block.getAccessKey().equalsIgnoreCase(newBlock.getAccessKey())) {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded) {
                /* TODO: Only add the player when he already got checked */
                chatBlocks.add(newBlock);
            }

            /* Delete the first ten elements of the chatBuffer */
            chatBuffer.subList(0, 10).clear();
        }
        /* ---------------------------------------------------------------------------------------------------------- */
        for (ChatBlock block : chatBlocks) {
            String blockKey = block.getAccessKey();
            for (int i = 0; i < playerList.size(); i++) {
                StatsPlayer player = playerList.get(i);
                String playerName = player.getName();
                boolean playerChecked = player.isChecked();
                if (playerName.equalsIgnoreCase(blockKey)) {
                }
            }
        }
    }
}