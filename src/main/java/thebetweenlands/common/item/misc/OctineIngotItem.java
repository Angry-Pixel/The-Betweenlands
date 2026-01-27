package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class OctineIngotItem extends HoverTextItem {
	public OctineIngotItem(Properties properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, new OctineIngotDispenseBehaviour());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (result.getType() == HitResult.Type.BLOCK) {
			TinderResult tinder = getTinder(level, result.getBlockPos(), result.getDirection());
			if (tinder.hasTinder() && !level.getBlockState(tinder.tinderPos()).is(Blocks.FIRE)) {
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

				TinderResult tinder = getTinder(level, pos, result.getDirection());
				
				if (tinder.hasTinder()) {
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

								if (level.getBlockState(tinder.tinderPos().below()).is(BlockRegistry.PEAT)) {
									AdvancementCriteriaRegistry.PEAT_FIRE.get().trigger(sp);
								}
							}

							if (tinder.isBlockTinder() || level.getBlockState(tinder.tinderPos()).canBeReplaced()) {
								level.setBlockAndUpdate(tinder.tinderPos(), Blocks.FIRE.defaultBlockState());
							}
							
							level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}

	public static record TinderResult(boolean hasTinder, boolean isBlockTinder, List<ItemEntity> tinderItems, BlockPos tinderPos) {
		public static final TinderResult EMPTY = new TinderResult(false, false, List.of(), BlockPos.ZERO);
	}
	
	public static TinderResult getTinder(Level level, BlockPos pos, @Nullable Direction face) {
		if(!level.isLoaded(pos)) {
			return TinderResult.EMPTY;
		}

		// Check if the block is tinder
		BlockState blockState = level.getBlockState(pos);
		if (isTinder(ItemStack.EMPTY, blockState)) {
			return new TinderResult(true, true, List.of(), pos);
		}
		
		// If we can replace the block (or no face was specified), check for tinder items inside it
		if(face == null || blockState.canBeReplaced()) {
			List<ItemEntity> tinderItems = getTinderItems(level, pos);
			if(!tinderItems.isEmpty()) {
				return new TinderResult(true, false, tinderItems, pos);
			}
		}
		
		// If a face was specified, check for tinder items in the block adjacent to that face
		if(face != null) {
			BlockPos itemPos = pos.relative(face);
			List<ItemEntity> tinderItems = getTinderItems(level, itemPos);
			if(!tinderItems.isEmpty()) {
				return new TinderResult(true, false, tinderItems, itemPos);
			}
		}
		
		return TinderResult.EMPTY;
	}
	
	public static List<ItemEntity> getTinderItems(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(ItemEntity.class, new AABB(pos), entity -> entity.isAlive() && !entity.getItem().isEmpty() && isTinder(entity.getItem(), null));
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
	
	public static class OctineIngotDispenseBehaviour extends OptionalDispenseItemBehavior {
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			Level level = source.level();
			Direction facing = source.state().getValue(DispenserBlock.FACING);
			BlockPos pos = source.pos().relative(facing);

			// Check for tinder in front of the dispenser
			TinderResult tinder = getTinder(level, pos, null);
			BlockPos tinderPos = tinder.tinderPos();

			// Check there's tinder and we're not igniting the wrong block
			if(!tinder.hasTinder() || !Objects.equals(tinderPos, pos)) {
				this.setSuccess(false);
				return stack;
			}

			// Check the block can actually be ignited
			if(!tinder.isBlockTinder() && !level.getBlockState(tinderPos).canBeReplaced()) {
				this.setSuccess(false);
				return stack;
			}

			// Ignite the block
			// Note: doesn't check if the fire block can actually be placed,
			//       but the normal octine ingot doesn't either
			this.setSuccess(true);
			level.setBlockAndUpdate(tinder.tinderPos(), Blocks.FIRE.defaultBlockState());

			return stack;
		}
		
		@Override
		protected void playSound(BlockSource blockSource) {
			super.playSound(blockSource);
			if(this.isSuccess()) {
				blockSource.level().playSound(null, blockSource.pos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
	}
}
