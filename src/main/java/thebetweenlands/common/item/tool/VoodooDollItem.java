package thebetweenlands.common.item.tool;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.common.registries.DamageTypeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class VoodooDollItem extends Item {
	public VoodooDollItem(Properties properties) {
		super(properties);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 40;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(level, player, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if(entity instanceof Player player) {
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(5.0D), living -> living.isAlive() && !living.getType().is(Tags.EntityTypes.BOSSES) && !(living instanceof Player));
			list.remove(player);

			boolean attacked = false;
			for (LivingEntity living : list) {
				DamageSource source = level.damageSources().source(DamageTypeRegistry.VOODOO);
				if (!level.isClientSide()) {
					attacked |= living.hurt(source, 20);
				} else if (!living.isInvulnerableTo(source)) {
					attacked = true;
					for (int i = 0; i < 20; i++) {
//						BLParticles.SWAMP_SMOKE.spawn(level, living.getX(), living.getY() + living.getBbHeight() / 2.0D, living.getZ(), ParticleFactory.ParticleArgs.get().withMotion((level.getRandom().nextFloat() - 0.5F) * 0.5F, (level.getRandom().nextFloat() - 0.5F) * 0.5F, (level.getRandom().nextFloat() - 0.5F) * 0.5F).withColor(1, 1, 1, 1));
					}
				}
			}

			if (!level.isClientSide() && attacked) {
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
				level.playSound(null, player.blockPosition(), SoundRegistry.VOODOO_DOLL.get(), SoundSource.PLAYERS, 0.5F, 1.0F - level.getRandom().nextFloat() * 0.3F);
			}
		}

		return stack;
	}
}
