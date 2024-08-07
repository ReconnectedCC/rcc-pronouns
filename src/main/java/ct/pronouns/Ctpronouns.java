package ct.pronouns;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;


public class Ctpronouns implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("ct-pronouns");

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(Pronouns::onServerStart);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("pronouns")
						.executes(context -> {
							context.getSource().sendFeedback(() -> Text.literal("Usage: /pronouns <pronoun1 or 'clear'> <pronoun2 (optional)> (Accepted pronouns: he/him/she/her/it/its/they/them/any/ask/avoid/other)"), false);
                            return 0;
                        })
				.then(argument("pronouns", greedyString())
						.executes(context -> {
					LOGGER.info("Pronouns command called");
					String pronouns = StringArgumentType.getString(context, "pronouns");
					String[] pList = pronouns.split("(\\s|,|/)", 2);
					if (pList.length == 1) {
						context.getSource().sendFeedback(() -> Text.literal(Pronouns.setPronouns(context.getSource(),pronouns)), false);
					} else {
						context.getSource().sendFeedback(() -> Text.literal(Pronouns.setPronouns(context.getSource(),pList[0],pList[1])), false);
					}
					return 1;
                }))));
	}
}