package thebetweenlands.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class XPGivingFoodItem extends HoverTextItem {

	private final int xpAmount;

	public XPGivingFoodItem(int xpAmount, Properties properties) {
		super(properties);
		this.xpAmount = xpAmount;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		if (this.xpAmount > 0 && livingEntity instanceof Player player)
			if (!level.isClientSide()) {
				player.giveExperiencePoints(this.xpAmount);
				level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F + level.getRandom().nextFloat() * 0.4F);
			} else {
				for (int i = 0; i < 20; i++) {
//					BLParticles.XP_PIECES.spawn(level, player.getX() + level.getRandom().nextFloat() * 0.6F - 0.3F, player.getY() + player.getEyeHeight() - 0.1F + level.getRandom().nextFloat() * 0.6F - 0.3F, player.getZ() + level.getRandom().nextFloat() * 0.6F - 0.3F);
				}
			}
		return super.finishUsingItem(stack, level, livingEntity);
	}
}
