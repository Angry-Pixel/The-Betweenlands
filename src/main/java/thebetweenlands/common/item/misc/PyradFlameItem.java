package thebetweenlands.common.item.misc;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.projectile.PyradFlame;

public class PyradFlameItem extends Item {
	public PyradFlameItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide()) {
			level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);

			Vec3 look = player.getLookAngle();

			float f = 0.05F;

			for (int i = 0; i < player.getRandom().nextInt(6) + 1; ++i) {
				PyradFlame flame = new PyradFlame(level, player, new Vec3(look.x + player.getRandom().nextGaussian() * (double)f, look.y, look.z + player.getRandom().nextGaussian() * (double)f));
				flame.setY(player.getY() + (double)(player.getBbHeight() / 2.0F) + 0.5D);
				level.addFreshEntity(flame);
			}
			stack.consume(1, player);
		}
		return InteractionResultHolder.success(stack);
	}
}
