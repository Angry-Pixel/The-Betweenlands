package thebetweenlands.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
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

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack emptyMapStack = player.getItemInHand(hand);
		if (level.isClientSide()) {
			return InteractionResultHolder.pass(emptyMapStack);
		} else {
			if (level.dimensionTypeRegistration().is(DimensionRegistries.BETWEENLANDS_DIMENSION_TYPE_KEY)) {
				emptyMapStack.consume(1, player);
				player.awardStat(Stats.ITEM_USED.get(this));
				player.level().playSound(null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundSource(), 1.0F, 1.0F);
				ItemStack itemstack1 = AmateMapItem.setupNewMap(level, player.getBlockX(), player.getBlockZ(), true, false);
				if (emptyMapStack.isEmpty()) {
					return InteractionResultHolder.consume(itemstack1);
				} else {
					if (!player.getInventory().add(itemstack1.copy())) {
						player.drop(itemstack1, false);
					}

					return InteractionResultHolder.consume(emptyMapStack);
				}
			} else {
				if (!level.isClientSide()) {
					player.displayClientMessage(Component.translatable("item.thebetweenlands.amate_map.invalid").withStyle(ChatFormatting.RED), false);
				}
				return InteractionResultHolder.fail(emptyMapStack);
			}
		}
	}
}
