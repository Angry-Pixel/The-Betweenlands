package thebetweenlands.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import java.util.List;

public class EventCommand {

	private static final SimpleCommandExceptionType NOT_IN_BETWEENLANDS = new SimpleCommandExceptionType(Component.translatable("commands.thebetweenlands.event.not_in_bl"));

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("event")
			.requires(cs -> cs.hasPermission(Commands.LEVEL_ADMINS))
			.then(Commands.literal("toggle")
				.then(Commands.argument("event", StringArgumentType.string())
					.suggests((context, builder) -> SharedSuggestionProvider.suggestResource(getEventNames(), builder))
					.executes(context -> toggleEventForWorld(context.getSource(), ResourceLocation.parse(StringArgumentType.getString(context, "event"))))))
			.then(Commands.literal("on")
				.then(Commands.argument("event", StringArgumentType.string())
					.suggests((context, builder) -> SharedSuggestionProvider.suggestResource(getEventsForState(false), builder))
					.executes(context -> toggleEventForWorld(context.getSource(), ResourceLocation.parse(StringArgumentType.getString(context, "event")), true))))
			.then(Commands.literal("off")
				.executes(EventCommand::toggleAllOff)
				.then(Commands.argument("event", StringArgumentType.string())
					.suggests((context, builder) -> SharedSuggestionProvider.suggestResource(getEventsForState(true), builder))
					.executes(context -> toggleEventForWorld(context.getSource(), ResourceLocation.parse(StringArgumentType.getString(context, "event")), false))))
			.then(Commands.literal("disable")
				.executes(EventCommand::disableEvents))
			.then(Commands.literal("enable")
				.executes(EventCommand::enableEvents));

	}

	private static int toggleEventForWorld(CommandSourceStack source, ResourceLocation eventName) throws CommandSyntaxException {
		performBasicChecks(source, eventName);

		EnvironmentEvent event = BLRegistries.ENVIRONMENT_EVENTS.get(eventName);
		boolean isActive = event.isActive();
		event.setActive(source.getLevel(), !isActive);
		if (event.isActive() != isActive) {
			source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.event." + (isActive ? "off" : "on"), Component.translatable(event.getDescriptionId())), false);
			return Command.SINGLE_SUCCESS;
		} else {
			source.sendFailure(Component.translatable("commands.thebetweenlands.event.fail_" + (isActive ? "off" : "on"), Component.translatable(event.getDescriptionId())));
			return 0;
		}
	}

	private static int toggleAllOff(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if (context.getSource().getLevel().dimension() != DimensionRegistries.DIMENSION_KEY) {
			throw NOT_IN_BETWEENLANDS.create();
		}

		BLEnvironmentEventRegistry environmentEventRegistry = BetweenlandsWorldStorage.get(context.getSource().getLevel()).getEnvironmentEventRegistry();
		for (EnvironmentEvent event : environmentEventRegistry.getActiveEvents()) {
			event.setActive(context.getSource().getLevel(), false);
		}
		context.getSource().sendSuccess(() -> Component.translatable("commands.thebetweenlands.event.all_off"), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int toggleEventForWorld(CommandSourceStack source, ResourceLocation eventName, boolean active) throws CommandSyntaxException {
		performBasicChecks(source, eventName);

		EnvironmentEvent event = BLRegistries.ENVIRONMENT_EVENTS.get(eventName);
		if (event.isActive() == active) {
			source.sendFailure(Component.translatable("commands.thebetweenlands.event.already_" + (active ? "off" : "on"), Component.translatable(event.getDescriptionId())));
			return 0;
		}
		event.setActive(source.getLevel(), active);
		if (event.isActive() != active) {
			source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.event." + (active ? "off" : "on"), Component.translatable(event.getDescriptionId())), false);
			return Command.SINGLE_SUCCESS;
		} else {
			source.sendFailure(Component.translatable("commands.thebetweenlands.event.fail_" + (active ? "off" : "on"), Component.translatable(event.getDescriptionId())));
			return 0;
		}
	}

	private static int enableEvents(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if (context.getSource().getLevel().dimension() != DimensionRegistries.DIMENSION_KEY) {
			throw NOT_IN_BETWEENLANDS.create();
		}
		BLEnvironmentEventRegistry environmentEventRegistry = BetweenlandsWorldStorage.get(context.getSource().getLevel()).getEnvironmentEventRegistry();
		if (environmentEventRegistry.isEnabled()) {
			context.getSource().sendFailure(Component.translatable("commands.thebetweenlands.event.already_enabled"));
			return 0;
		}
		environmentEventRegistry.enable();
		context.getSource().sendSuccess(() -> Component.translatable("commands.thebetweenlands.event.enable"), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int disableEvents(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if (context.getSource().getLevel().dimension() != DimensionRegistries.DIMENSION_KEY) {
			throw NOT_IN_BETWEENLANDS.create();
		}
		BLEnvironmentEventRegistry environmentEventRegistry = BetweenlandsWorldStorage.get(context.getSource().getLevel()).getEnvironmentEventRegistry();
		if (environmentEventRegistry.isDisabled()) {
			context.getSource().sendFailure(Component.translatable("commands.thebetweenlands.event.already_disabled"));
			return 0;
		}
		environmentEventRegistry.disable();
		for (EnvironmentEvent event : environmentEventRegistry.getActiveEvents()) {
			event.setActive(context.getSource().getLevel(), false);
		}
		context.getSource().sendSuccess(() -> Component.translatable("commands.thebetweenlands.event.disable"), false);
		return Command.SINGLE_SUCCESS;
	}

	private static void performBasicChecks(CommandSourceStack source, ResourceLocation eventName) throws CommandSyntaxException {
		if (source.getLevel().dimension() != DimensionRegistries.DIMENSION_KEY) {
			throw NOT_IN_BETWEENLANDS.create();
		}

		if (!BLRegistries.ENVIRONMENT_EVENTS.containsKey(eventName)) {
			throw new SimpleCommandExceptionType(Component.translatable("commands.thebetweenlands.event.invalid_event", eventName)).create();
		}
	}

	private static List<ResourceLocation> getEventNames() {
		return BLRegistries.ENVIRONMENT_EVENTS.stream().map(BLRegistries.ENVIRONMENT_EVENTS::getKey).toList();
	}

	private static List<ResourceLocation> getEventsForState(boolean active) {
		return BLRegistries.ENVIRONMENT_EVENTS.stream().filter(event -> event.isActive() == active).map(BLRegistries.ENVIRONMENT_EVENTS::getKey).toList();
	}
}
