package thebetweenlands.common.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.AttachmentRegistry;

public class FumigantItem extends HoverTextItem {
	public FumigantItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			this.removeSmell(player, stack);
		} else {
			for (int count = 0; count <= 5; ++count) {
//				BLParticles.FANCY_BUBBLE.spawn(level, player.getX() + (level.getRandom().nextDouble() - 0.5D), player.getY() + 1D + level.getRandom().nextDouble(), player.getZ() + (level.getRandom().nextDouble() - 0.5D));
			}
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	public void removeSmell(Player player, ItemStack stack) {
		RotSmellData data = player.getData(AttachmentRegistry.ROT_SMELL);
		if (data.isSmellingBad(player)) {
			data.setNotSmellingBad(player);
			data.setImmune(player, Math.max(data.getRemainingImmunityTicks(player), 600));
			stack.consume(1, player);
			if(player instanceof ServerPlayer sp)
				AdvancementCriteriaRegistry.USED_FUMIGANT.get().trigger(sp);
		}
	}
}
