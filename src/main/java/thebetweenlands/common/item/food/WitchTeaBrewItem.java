package thebetweenlands.common.item.food;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.FoodSickness;

public class WitchTeaBrewItem extends Item {
	public WitchTeaBrewItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && entity instanceof Player player) {
			FoodSicknessData data = player.getData(AttachmentRegistry.FOOD_SICKNESS);
			if (FoodSickness.getSicknessForHatred(data.getFoodHatred(this)) != FoodSickness.SICK) {
				data.increaseFoodHatred(player, this, 0, FoodSickness.SICK.maxHatred);
			}
//			for (int count = 0; count < 4; count++) {
//				TinySludgeWormHelper worm = new TinySludgeWormHelper(level);
//				worm.moveTo(player.getX(), player.getY() + 1D, player.getZ(), player.getYRot(), player.getXRot());
//				worm.setOwner(player.getUUID());
//				level.addFreshEntity(worm);
//			}
		}
		return super.finishUsingItem(stack, level, entity);
	}
}
