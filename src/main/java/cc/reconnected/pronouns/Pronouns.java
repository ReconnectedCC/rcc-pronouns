package cc.reconnected.pronouns;

import cc.reconnected.server.RccServer;
import cc.reconnected.server.database.PlayerData;
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
            String pronouns = Pronouns.getPronouns(player.getUniqueId());
            if (pronouns.isBlank()) {
                return "";
            }
            return pronouns.split("(\\s|,|/)", 2)[0];
        });
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%pronoun2%",500, (player) -> {
            if (player == null) {
                return "";
            }
            String pronouns = Pronouns.getPronouns(player.getUniqueId());
            if (pronouns.isBlank()) {
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
        if (!player.isExecutedByPlayer()) return "This command can only be run by a player.";
        if (Objects.equals(pronoun, "clear")) {
            //Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), "");
            PlayerData pData = PlayerData.getPlayer(Objects.requireNonNull(player.getPlayer()).getUuid());
            pData.set(PlayerData.KEYS.pronouns,"");
            return "Cleared Pronouns";
        }
        if (!validatePronoun(pronoun)) {
            return "Invalid pronouns.";
        }
        //Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), " ["+pronoun+"]");
        PlayerData pData = PlayerData.getPlayer(Objects.requireNonNull(player.getPlayer()).getUuid());
        pData.set(PlayerData.KEYS.pronouns,pronoun+",");
        return "Changed Pronouns";

    }
    public static String setPronouns(ServerCommandSource player,String pronoun1, String pronoun2) {
        if (!player.isExecutedByPlayer()) return "This command can only be run by a player.";
        if (!validatePronoun(pronoun1) || !validatePronoun(pronoun2)) {
            return "Invalid pronouns.";
        }
        //Objects.requireNonNull(TabAPI.getInstance().getTabListFormatManager()).setSuffix(TabAPI.getInstance().getPlayer(player.getName()), " ["+pronoun1+"/"+pronoun2+"]");
        PlayerData pData = PlayerData.getPlayer(Objects.requireNonNull(player.getPlayer()).getUuid());
        pData.set(PlayerData.KEYS.pronouns,pronoun1 + "/" + pronoun2);
        return "Changed Pronouns";
    }

    public static String getPronouns(UUID player) {
        PlayerData pData = PlayerData.getPlayer(player);
        String pronouns = pData.get(PlayerData.KEYS.pronouns);
        if (pronouns == null || pronouns.isBlank()) {
            return ",";
        }
        return pronouns;
    }
}
