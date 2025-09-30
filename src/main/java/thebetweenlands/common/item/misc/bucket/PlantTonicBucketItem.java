package thebetweenlands.common.item.misc.bucket;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;

public class PlantTonicBucketItem extends Item {
	public PlantTonicBucketItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (level.getBlockState(pos).is(BlockTags.CROPS)) {
			while (level.getBlockState(pos).is(BlockTags.CROPS)) {
				pos = pos.below();
			}
		}

		if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity) {
			boolean cured = false;

			for (int xo = -2; xo <= 2; xo++) {
				for (int yo = -2; yo <= 2; yo++) {
					for (int zo = -2; zo <= 2; zo++) {
						BlockPos offsetPos = pos.offset(xo, yo, zo);
						if (level.getBlockEntity(offsetPos) instanceof DugSoilBlockEntity soil && soil.getDecay() > 0) {
							cured = true;
							if (!level.isClientSide()) {
								soil.setDecay(level, offsetPos, 0);
								CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) context.getPlayer(), offsetPos, stack);
							} else {
								ParticleUtils.spawnParticles(level, pos, 18, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
							}
						}
					}
				}
			}

			if (cured) {
				ItemStack newStack = stack.hurtAndConvertOnBreak(1, this.getCraftingRemainingItem(stack).getItem(), context.getPlayer(), LivingEntity.getSlotForHand(context.getHand()));
				level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1, 1);
				if (!context.getPlayer().isCreative() && !newStack.is(stack.getItem())) {
					context.getPlayer().setItemInHand(context.getHand(), newStack);
				}
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}
}
