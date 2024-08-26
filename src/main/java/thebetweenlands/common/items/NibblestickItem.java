package thebetweenlands.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.FoodSickness;

public class NibblestickItem extends Item {
	public NibblestickItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (livingEntity instanceof Player player) {
			FoodSicknessData data = player.getData(AttachmentRegistry.FOOD_SICKNESS);
			if (FoodSickness.getSicknessForHatred(data.getFoodHatred(this)) != FoodSickness.SICK) {
				int xp = level.getRandom().nextInt(4);
				if (xp > 0) {
					if (!level.isClientSide()) {
						data.increaseFoodHatred(player, this, 4, 0); //Increased food sickness speed
						player.giveExperiencePoints(xp);
						level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F + level.getRandom().nextFloat() * 0.4F);
					} else {
						for (int i = 0; i < 20; i++) {
//							BLParticles.XP_PIECES.spawn(level, player.getX() + level.getRandom().nextFloat() * 0.6F - 0.3F, player.getY() + player.getEyeHeight() - 0.1F + level.getRandom().nextFloat() * 0.6F - 0.3F, player.getZ() + level.getRandom().nextFloat() * 0.6F - 0.3F);
						}
					}
				}
			}
		}
		return super.finishUsingItem(stack, level, livingEntity);
	}
}
