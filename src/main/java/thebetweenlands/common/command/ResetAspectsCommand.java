package thebetweenlands.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import thebetweenlands.common.herblore.aspect.AspectManager;

public class ResetAspectsCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("resetAspects")
			.requires(cs -> cs.hasPermission(Commands.LEVEL_ADMINS))
			.executes(context -> confirm(context.getSource()))
			.then(Commands.literal("confirm")
				.executes(context -> reset(context.getSource())));
	}

	private static int confirm(CommandSourceStack source) {
		source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.reset_aspects.confirm"), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int reset(CommandSourceStack source) {
		AspectManager.get(source.getLevel()).resetStaticAspects(source.registryAccess(), AspectManager.getAspectsSeed(source.getLevel().getSeed()));
		source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.reset_aspects.reset"), false);
		return Command.SINGLE_SUCCESS;
	}
}
