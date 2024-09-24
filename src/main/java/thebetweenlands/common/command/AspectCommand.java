package thebetweenlands.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.List;

public class AspectCommand {

	private static final SimpleCommandExceptionType EMPTY_HAND = new SimpleCommandExceptionType(Component.translatable("commands.thebetweenlands.aspect.empty_hand"));
	private static final SimpleCommandExceptionType NO_BOOK = new SimpleCommandExceptionType(Component.translatable("commands.thebetweenlands.aspect.no_book"));

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("aspect")
			.requires(cs -> cs.hasPermission(Commands.LEVEL_ADMINS))
			.then(Commands.literal("discover")
				.then(Commands.literal("held")
					.executes(context -> discoverAspects(context.getSource(), true)))
				.then(Commands.literal("all")
					.executes(context -> discoverAspects(context.getSource(), false))))
			.then(Commands.literal("reset")
				.then(Commands.literal("held")
					.executes(context -> resetAspects(context.getSource(), true)))
				.then(Commands.literal("all")
					.executes(context -> resetAspects(context.getSource(), false))));
	}

	private static int discoverAspects(CommandSourceStack source, boolean checkHand) throws CommandSyntaxException {
		AspectManager manager = AspectManager.get(source.getLevel());
		ServerPlayer player = source.getPlayerOrException();

		if (!DiscoveryContainerData.hasDiscoveryProvider(player)) {
			throw NO_BOOK.create();
		}

		if (checkHand) {
			ItemStack stack = player.getMainHandItem();
			if (stack.isEmpty()) throw EMPTY_HAND.create();

			DiscoveryContainerData mergedKnowledge = DiscoveryContainerData.getMergedDiscoveryContainer(source.getPlayerOrException());
			DiscoveryContainerData.AspectDiscovery discovery = mergedKnowledge.discover(player, manager, AspectManager.getAspectItem(stack, source.registryAccess()));
			if (discovery.discovered != null) {
				DiscoveryContainerData.addDiscoveryToContainers(player, AspectManager.getAspectItem(source.getPlayerOrException().getMainHandItem(), source.registryAccess()), discovery.discovered.type());
				source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.aspect.discover_held", Component.literal(discovery.result.toString()), Component.literal(discovery.discovered.type().getRegisteredName())), false);
				return Command.SINGLE_SUCCESS;
			} else {
				source.sendFailure(Component.translatable("commands.thebetweenlands.aspect.no_aspects_held"));
				return 0;
			}
		} else {
			List<ItemStack> discoveryContainers = DiscoveryContainerData.getWritableDiscoveryContainers(player);
			for (ItemStack container : discoveryContainers)
				DiscoveryContainerData.executeDiscoveryAction(container, data -> data.discoverAll(manager));
			AdvancementCriteriaRegistry.HERBLORE_FIND_ALL.get().trigger(player);
			source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.aspect.discover_all"), false);
			return Command.SINGLE_SUCCESS;
		}
	}

	private static int resetAspects(CommandSourceStack source, boolean checkHand) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!DiscoveryContainerData.hasDiscoveryProvider(player)) {
			throw NO_BOOK.create();
		}
		List<ItemStack> discoveryContainers = DiscoveryContainerData.getWritableDiscoveryContainers(player);
		if (checkHand) {
			ItemStack stack = player.getMainHandItem();
			if (stack.isEmpty()) throw EMPTY_HAND.create();

			for (ItemStack container : discoveryContainers)
				DiscoveryContainerData.executeDiscoveryAction(container, data -> data.resetDiscovery(AspectManager.getAspectItem(stack, source.registryAccess())));
			source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.aspect.reset_held"), false);
		} else {
			for (ItemStack container : discoveryContainers)
				DiscoveryContainerData.executeDiscoveryAction(container, DiscoveryContainerData::resetAllDiscoveries);
			source.sendSuccess(() -> Component.translatable("commands.thebetweenlands.aspect.reset_all"), false);
		}
		return Command.SINGLE_SUCCESS;
	}
}
