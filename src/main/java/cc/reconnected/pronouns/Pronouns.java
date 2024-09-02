package cc.reconnected.pronouns;

import cc.reconnected.server.RccServer;
import cc.reconnected.server.database.PlayerData;
import cc.reconnected.server.database.PlayerTable;
import me.neznamy.tab.api.TabAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Pronouns {
    static MinecraftServer server;
    static Logger LOGGER = LoggerFactory.getLogger("rcc-pronouns");
    public static void onServerStart(MinecraftServer server) {
        Pronouns.server = server;
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%pronoun1%",500, (player) -> {
            if (player == null) {
                return "";
            }
            String pronouns = Pronouns.getPronouns(player.getUniqueId());
            if (pronouns == null) {
                return "";
            }
            return pronouns.split("(\\s|,|/)", 2)[0];
        });
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%pronoun2%",500, (player) -> {
            if (player == null) {
                return "";
            }
            String pronouns = Pronouns.getPronouns(player.getUniqueId());
            if (pronouns == null) {
                return "";
            }
            return pronouns.split("(\\s|,|/)", 2)[1];
        });
        /*
        Objects.requireNonNull(TabAPI.getInstance().getEventBus()).register(PlayerLoadEvent.class, event -> {
            TabPlayer tabPlayer = event.getPlayer();
            String pronouns = Pronouns.getPronouns(tabPlayer.getUniqueId());
            if (pronouns == null) {
                return;
            }
            Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(tabPlayer, " ["+pronouns+"]");
    });
        */
    }
    public static Boolean validatePronoun(String pronoun) {
        return Pattern.matches("^(he|him|she|her|it|its|they|them|any|ask|avoid|other)$", pronoun);
    }
    public static String setPronouns(ServerCommandSource player,String pronoun) {
        if (Objects.equals(pronoun, "clear")) {
            //Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), "");
            PlayerTable table = RccServer.getInstance().playerTable();
            PlayerData pData = table.getPlayerData(Objects.requireNonNull(player.getPlayer()).getUuid());
            if (pData == null) {
                return "Warning: Player Data Missing, this change will not survive a server restart.";
            }
            pData.pronouns("");
            table.updatePlayerData(pData);
            return "Cleared Pronouns";
        }
        if (!validatePronoun(pronoun)) {
            return "Invalid pronouns.";
        }
        //Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), " ["+pronoun+"]");
        PlayerTable table = RccServer.getInstance().playerTable();
        PlayerData pData = table.getPlayerData(Objects.requireNonNull(player.getPlayer()).getUuid());
        if (pData == null) {
            return "Error: Player Data Missing. Try again later.";
        }
        pData.pronouns(pronoun+",");
        table.updatePlayerData(pData);
        return "Changed Pronouns";

    }
    public static String setPronouns(ServerCommandSource player,String pronoun1, String pronoun2) {
        if (!validatePronoun(pronoun1) || !validatePronoun(pronoun2)) {
            return "Invalid pronouns.";
        }
        //Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), " ["+pronoun1+"/"+pronoun2+"]");
        PlayerTable table = RccServer.getInstance().playerTable();
        PlayerData pData = table.getPlayerData(Objects.requireNonNull(player.getPlayer()).getUuid());
        if (pData == null) {
            return "Error: Player Data Missing. Try again later.";
        }
        pData.pronouns(pronoun1 + "/" + pronoun2);
        table.updatePlayerData(pData);
        return "Changed Pronouns";
    }

    public static String getPronouns(UUID player) {
        PlayerTable table = RccServer.getInstance().playerTable();
        PlayerData pData = table.getPlayerData(player);
        if (pData == null) {
            return null;
        }
        if (pData.pronouns().isBlank()) {
            return ",";
        }
        return pData.pronouns();
    }
}
