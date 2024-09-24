package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class OctineIngotItem extends HoverTextItem {
	public OctineIngotItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockPos offsetPos = result.getBlockPos().relative(result.getDirection());
			boolean hasTinder = false;
			BlockState blockState = level.getBlockState(result.getBlockPos());
			if (isTinder(ItemStack.EMPTY, blockState)) {
				hasTinder = true;
			} else {
				List<ItemEntity> tinder = level.getEntitiesOfClass(ItemEntity.class, new AABB(offsetPos), entity -> !entity.getItem().isEmpty() && isTinder(entity.getItem(), null));
				if (!tinder.isEmpty()) {
					hasTinder = true;
				}
			}
			if (hasTinder && !blockState.is(Blocks.FIRE)) {
				player.startUsingItem(hand);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
			}
		}
		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
		if (living instanceof Player player) {
			BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos pos = result.getBlockPos();
				BlockPos offsetPos = pos.relative(result.getDirection());
				boolean hasTinder = false;
				boolean isBlockTinder = false;
				BlockState blockState = level.getBlockState(pos);
				if (isTinder(ItemStack.EMPTY, blockState)) {
					hasTinder = true;
					isBlockTinder = true;
				} else {
					List<ItemEntity> tinder = level.getEntitiesOfClass(ItemEntity.class, new AABB(offsetPos), entity -> !entity.getItem().isEmpty() && isTinder(entity.getItem(), null));
					if (!tinder.isEmpty()) {
						hasTinder = true;
					}
				}
				if (hasTinder) {
					if (level.getRandom().nextInt(remainingUseDuration / 10 + 1) == 0) {
						level.addParticle(ParticleTypes.SMOKE,
							result.getLocation().x + level.getRandom().nextFloat() * 0.2 - 0.1,
							result.getLocation().y + level.getRandom().nextFloat() * 0.2 - 0.1,
							result.getLocation().z + level.getRandom().nextFloat() * 0.2 - 0.1, 0, 0.1, 0);
						level.addParticle(ParticleTypes.FLAME,
							result.getLocation().x + level.getRandom().nextFloat() * 0.2 - 0.1,
							result.getLocation().y + level.getRandom().nextFloat() * 0.2 - 0.1,
							result.getLocation().z + level.getRandom().nextFloat() * 0.2 - 0.1, 0, 0.1, 0);
					}
					if (!level.isClientSide()) {
						if (remainingUseDuration <= 1) {
							if (player instanceof ServerPlayer sp) {
								AdvancementCriteriaRegistry.OCTINE_INGOT_FIRE.get().trigger(sp);

								if (level.getBlockState(isBlockTinder ? pos.below() : offsetPos.below()).is(BlockRegistry.PEAT)) {
									AdvancementCriteriaRegistry.PEAT_FIRE.get().trigger(sp);
								}
							}

							if (isBlockTinder) {
								level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
							} else {
								if (level.getBlockState(offsetPos).canBeReplaced()) {
									level.setBlockAndUpdate(offsetPos, Blocks.FIRE.defaultBlockState());
								}
							}
							level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}

	public static boolean isTinder(ItemStack stack, @Nullable BlockState state) {
		if (state != null) {
			return state.is(BLBlockTagProvider.OCTINE_IGNITES);
		}
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof BlockItem block) {
				return isTinder(ItemStack.EMPTY, block.getBlock().defaultBlockState());
			}
			return stack.is(BLItemTagProvider.OCTINE_IGNITES);
		}
		return false;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 32;
	}
}
