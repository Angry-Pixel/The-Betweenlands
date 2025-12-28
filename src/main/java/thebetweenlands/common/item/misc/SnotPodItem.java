package thebetweenlands.common.item.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import thebetweenlands.common.entity.monster.RockSnot;
import thebetweenlands.common.registries.EntityRegistry;

public class SnotPodItem extends Item {
	public SnotPodItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		if (!context.getLevel().isClientSide() && context.getClickedFace() == Direction.UP) {
			RockSnot snot = new RockSnot(EntityRegistry.ROCK_SNOT.get(), context.getLevel());
			snot.setPlacedByPlayer(true);
			snot.moveTo(context.getClickedPos().getX() + 0.5F, context.getClickedPos().getY() + 1F, context.getClickedPos().getZ() + 0.5F);
			if (context.getLevel().noCollision(snot)) {
				context.getLevel().addFreshEntity(snot);
				stack.consume(1, context.getPlayer());
				return InteractionResult.SUCCESS;
			}
		}
		return super.useOn(context);
	}
}
