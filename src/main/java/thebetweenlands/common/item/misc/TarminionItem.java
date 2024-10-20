package thebetweenlands.common.item.misc;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.projectile.ThrownTarminion;

public class TarminionItem extends Item {
	public TarminionItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			level.playSound(null, player.blockPosition(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 1, 1);

			ThrownTarminion tarminion = new ThrownTarminion(level, player);
			Vec3 lookVec = player.getLookAngle();
			tarminion.setPos(player.getX(), player.getEyeY(), player.getZ());
			tarminion.shoot(lookVec.x, lookVec.y, lookVec.z, 0.8F, 0.1F);
			level.addFreshEntity(tarminion);

			player.awardStat(Stats.ITEM_USED.get(this));
			itemStack.consume(1, player);
		}
		return super.use(level, player, hand);
	}
}
