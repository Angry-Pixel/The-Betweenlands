package thebetweenlands.common.item.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.projectile.AngryPebble;
import thebetweenlands.common.registries.SoundRegistry;

public class AngryPebbleItem extends Item {

	private final float explosionPower;

	public AngryPebbleItem(float explosionPower, Properties properties) {
		super(properties);
		this.explosionPower = explosionPower;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		level.playSound(null, player.blockPosition(), SoundRegistry.PEBBLE_HISS.get(), SoundSource.PLAYERS, 1.0F, 0.5F);
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
		if (entity instanceof Player player) {
			Vec3 forward = player.getLookAngle();
			float yaw = player.getYRot();
			float pitch = player.getXRot() - 90;
			float f = Mth.cos(-yaw * 0.017453292F - Mth.PI);
			float f1 = Mth.sin(-yaw * 0.017453292F - Mth.PI);
			float f2 = -Mth.cos(-pitch * 0.017453292F);
			float f3 = Mth.sin(-pitch * 0.017453292F);
			Vec3 up = new Vec3(f1 * f2, f3, f * f2);
			Vec3 right = forward.cross(up).normalize();
			Vec3 source = player.position().add(0, player.getEyeHeight() - 0.2F, 0).add(forward.scale(0.4F)).add(right.scale(0.3F));

			for(int i = 0; i < 5; i++) {
				level.addParticle(ParticleTypes.SMOKE, source.x + level.getRandom().nextFloat() * 0.5F - 0.25F, source.y + level.getRandom().nextFloat() * 0.5F - 0.25F, source.z + level.getRandom().nextFloat() * 0.5F - 0.25F, 0, 0, 0);
			}
		}

		super.onUseTick(level, entity, stack, remainingUseDuration);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
		if (!level.isClientSide() && entity instanceof Player player) {
			int useTime = this.getUseDuration(stack, entity) - timeCharged;

			if(useTime > 20) {
				level.playSound(null, player.blockPosition(), SoundRegistry.SORRY.get(), SoundSource.PLAYERS, 0.7F, 0.8F);
				AngryPebble pebble = new AngryPebble(player, level, stack, this.explosionPower);
				pebble.shootFromRotation(player, player.getXRot(), player.getYRot(), -10, 1.2F, 3.5F);
				level.addFreshEntity(pebble);
				stack.consume(1, player);
			}
		}
	}
}
