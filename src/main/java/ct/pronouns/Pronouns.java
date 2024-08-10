package ct.pronouns;

import ct.server.CtServer;
import ct.server.database.PlayerData;
import ct.server.database.PlayerTable;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Pronouns {
    static MinecraftServer server;
    static Logger LOGGER = LoggerFactory.getLogger("ct-pronouns");
    public static void onServerStart(MinecraftServer server) {
        Pronouns.server = server;
        Objects.requireNonNull(TabAPI.getInstance().getEventBus()).register(PlayerLoadEvent.class, event -> {
            TabPlayer tabPlayer = event.getPlayer();
            String pronouns = Pronouns.getPronouns(tabPlayer.getUniqueId());
            if (pronouns == null) {
                return;
            }
            Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(tabPlayer, " ["+pronouns+"]");
    });
    }
    public static Boolean validatePronoun(String pronoun) {
        return Pattern.matches("^(he|him|she|her|it|its|they|them|any|ask|avoid|other)$", pronoun);
    }
    public static String setPronouns(ServerCommandSource player,String pronoun) {
        LOGGER.info("Pronouns command called");
        if (Objects.equals(pronoun, "clear")) {
            Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), "");
            PlayerTable table = CtServer.getInstance().playerTable();
            PlayerData pData = table.getPlayerData(Objects.requireNonNull(player.getPlayer()).getUuid());
            if (pData == null) {
                return "Warning: Player Data Missing, this change will not survive a server restart.";
            }
            pData.pronouns("");
            table.updatePlayerData(pData);
            return "Your pronouns have been cleared";
        }
        if (!validatePronoun(pronoun)) {
            return "The pronouns that you provided are not settable by everyone. Please contact staff if you want these to be added manually.";
        }
        Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), " ["+pronoun+"]");
        PlayerTable table = CtServer.getInstance().playerTable();
        PlayerData pData = table.getPlayerData(Objects.requireNonNull(player.getPlayer()).getUuid());
        if (pData == null) {
            return "Warning: Player Data Missing, this change will not survive a server restart.";
        }
        pData.pronouns(pronoun);
        table.updatePlayerData(pData);
        return "Your pronouns have been updated";

    }
    public static String setPronouns(ServerCommandSource player,String pronoun1, String pronoun2) {
        LOGGER.info("Pronouns2 command called");
        if (!validatePronoun(pronoun1) || !validatePronoun(pronoun2)) {
            return "The pronouns that you provided are not settable by everyone. Please contact staff if you want these to be added manually.";
        }
        Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), "&r&7[&r"+pronoun1+"&7/&r"+pronoun2+"&7]&r");
        PlayerTable table = CtServer.getInstance().playerTable();
        PlayerData pData = table.getPlayerData(Objects.requireNonNull(player.getPlayer()).getUuid());
        if (pData == null) {
            return "Warning: Player Data Missing, this change will not survive a server restart.";
        }
        pData.pronouns(pronoun1 + "/" + pronoun2);
        table.updatePlayerData(pData);
        return "Your pronouns have been updated";
    }

    public static String getPronouns(UUID player) {
        PlayerTable table = CtServer.getInstance().playerTable();
        PlayerData pData = table.getPlayerData(player);
        if (pData == null) {
            return null;
        }
        return pData.pronouns();
    }
}
