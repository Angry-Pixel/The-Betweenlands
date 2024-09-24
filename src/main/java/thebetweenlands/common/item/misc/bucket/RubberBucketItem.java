package thebetweenlands.common.item.misc.bucket;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.item.misc.HoverTextItem;
import thebetweenlands.common.registries.ItemRegistry;

public class RubberBucketItem extends HoverTextItem {
	public RubberBucketItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide()) {
			player.awardStat(Stats.ITEM_USED.get(this));
			ItemStack rubber = new ItemStack(ItemRegistry.RUBBER_BALL.get(), 3);
			if (!player.getInventory().add(rubber)) {
				player.drop(rubber, false);
			}
		}
		player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
		return InteractionResultHolder.sidedSuccess(new ItemStack(ItemRegistry.SYRMORITE_BUCKET.get()), level.isClientSide());
	}
}
