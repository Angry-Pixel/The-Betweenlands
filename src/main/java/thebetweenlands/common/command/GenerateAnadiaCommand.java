package thebetweenlands.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.fishing.anadia.AnadiaParts;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.Collection;

public class GenerateAnadiaCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("generate_anadia")
			.requires(cs -> cs.hasPermission(2))
			.then(Commands.argument("targets", EntityArgument.players())
				.then(Commands.literal("random")
					.executes(context -> generateRandom(context.getSource(), EntityArgument.getPlayers(context, "targets"))))
				.then(Commands.literal("set")
					.then(Commands.argument("color", IntegerArgumentType.integer(0, 5))
						.then(Commands.argument("size", FloatArgumentType.floatArg(0.125F, 1.0F))
							.then(Commands.argument("head_type", IntegerArgumentType.integer(0, 2))
								.then(Commands.argument("body_type", IntegerArgumentType.integer(0, 2))
									.then(Commands.argument("tail_type", IntegerArgumentType.integer(0, 2))
										.then(Commands.argument("treasure", BoolArgumentType.bool())
											.then(Commands.argument("rotten", BoolArgumentType.bool())
												.executes(context -> generateSetFish(context.getSource(),
													EntityArgument.getPlayers(context, "targets"),
													(byte) IntegerArgumentType.getInteger(context, "color"),
													FloatArgumentType.getFloat(context, "size"),
													(byte) IntegerArgumentType.getInteger(context, "head_type"),
													(byte) IntegerArgumentType.getInteger(context, "body_type"),
													(byte) IntegerArgumentType.getInteger(context, "tail_type"),
													BoolArgumentType.getBool(context, "treasure"),
													BoolArgumentType.getBool(context, "rotten"))))))))))));
	}

	private static int generateRandom(CommandSourceStack source, Collection<ServerPlayer> players) {
		Anadia fake = new Anadia(EntityRegistry.ANADIA.get(), source.getLevel());
		ItemStack stack = new ItemStack(ItemRegistry.ANADIA.get());

		CompoundTag tag = new CompoundTag();

		fake.randomizeAnadiaProperties();
		fake.addAdditionalSaveData(tag);
		tag.putByte("fish_color", (byte) AnadiaParts.AnadiaColor.random(source.getLevel().getRandom()).ordinal());
		tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(fake.getType()).toString());
		BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(tag::remove);

		stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));

		for (ServerPlayer serverplayer : players) {
			if (serverplayer.getInventory().add(stack)) {
				serverplayer.drop(stack, false);
			}
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int generateSetFish(CommandSourceStack source, Collection<ServerPlayer> players, byte color, float size, byte headType, byte bodyType, byte tailType, boolean treasure, boolean rotten) {
		Anadia fake = new Anadia(EntityRegistry.ANADIA.get(), source.getLevel());
		ItemStack stack = new ItemStack(ItemRegistry.ANADIA.get());

		CompoundTag tag = new CompoundTag();

		fake.setHeadType(AnadiaParts.AnadiaHeadParts.get(headType));
		fake.setBodyType(AnadiaParts.AnadiaBodyParts.get(bodyType));
		fake.setTailType(AnadiaParts.AnadiaTailParts.get(tailType));
		fake.setHeadItem(Anadia.getPartFromLootTable(source.getLevel(), source.getPosition(), fake, LootTableRegistry.ANADIA_HEAD));
		fake.setBodyItem(Anadia.getPartFromLootTable(source.getLevel(), source.getPosition(), fake, LootTableRegistry.ANADIA_BODY));
		fake.setTailItem(Anadia.getPartFromLootTable(source.getLevel(), source.getPosition(), fake, LootTableRegistry.ANADIA_TAIL));
		fake.setFishSize(size);
		fake.setFishColor(AnadiaParts.AnadiaColor.get(color));
		fake.setIsTreasureFish(treasure);

		fake.addAdditionalSaveData(tag);
		tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(fake.getType()).toString());
		BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(tag::remove);

		stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));

		if (rotten) {
			stack.set(DataComponentRegistry.ROT_TIME, source.getLevel().getGameTime() + 19200);
		}

		for (ServerPlayer serverplayer : players) {
			if (serverplayer.getInventory().add(stack)) {
				serverplayer.drop(stack, false);
			}
		}

		return Command.SINGLE_SUCCESS;
	}
}
