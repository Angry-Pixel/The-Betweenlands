package thebetweenlands.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.DimensionRegistries;

public class EmptyAmateMapItem extends ComplexItem {

	public EmptyAmateMapItem(Properties properties) {
		super(properties);
	}

	// [VanillaCopy] EmptyMapItem.use, edits noted
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack emptyMapStack = player.getItemInHand(hand);
		if (level.isClientSide())
			return InteractionResultHolder.pass(emptyMapStack);

		//BL - only allow magic maps to be created in Betweenlands
		if (level.dimension() != DimensionRegistries.DIMENSION_KEY) {
			player.displayClientMessage(Component.translatable("item.thebetweenlands.amate_map.invalid").withStyle(ChatFormatting.RED), false);
			return InteractionResultHolder.fail(emptyMapStack);
		}

		emptyMapStack.consume(1, player);
		player.awardStat(Stats.ITEM_USED.get(this));
		player.level().playSound(null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundSource(), 1.0F, 1.0F);

		// BL - use custom setup method
		ItemStack newMapStack = AmateMapItem.setupNewMap(level, Mth.floor(player.getX()), Mth.floor(player.getZ()), true, false);

		if (emptyMapStack.isEmpty()) {
			return InteractionResultHolder.success(newMapStack);
		} else {
			if (!player.getInventory().add(newMapStack.copy())) {
				player.drop(newMapStack, false);
			}
			return InteractionResultHolder.success(emptyMapStack);
		}
	}
}
