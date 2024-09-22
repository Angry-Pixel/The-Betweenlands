package thebetweenlands.common.item.misc.bucket;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.*;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.container.RubberTapBlock;
import thebetweenlands.common.block.entity.InfuserBlockEntity;
import thebetweenlands.common.block.terrain.RubberLogBlock;
import thebetweenlands.common.component.item.InfusionBucketData;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Optional;

public class BLBucketItem extends Item {

	private final boolean allowHotFluids;
	private final Block tap;

	public BLBucketItem(boolean allowHotFluids, Block tap, Properties properties) {
		super(properties);
		this.allowHotFluids = allowHotFluids;
		this.tap = tap;
		DispenserBlock.registerBehavior(this, new BucketDispenseBehavior());
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		var copy = new ItemStack(this);
		copy.applyComponents(stack.getComponents());

		var tank = copy.getCapability(Capabilities.FluidHandler.ITEM);
		if (tank != null) {
			tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
		}

		return copy;
	}

	@Override
	public Component getName(ItemStack stack) {
		if (!stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).isEmpty()) {
			return Component.translatable(this.getDescriptionId() + ".fluid", Component.translatable(stack.get(DataComponentRegistry.STORED_FLUID).getFluidType().getDescriptionId()));
		}
		return super.getName(stack);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return isMilkBucket(stack) ? UseAnim.DRINK : UseAnim.NONE;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return isMilkBucket(stack) ? 32 : 0;
	}

	@Override
	public int getBurnTime(ItemStack stack, RecipeType<?> type) {
		var fluid = stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).copy();
		if (fluid.is(Fluids.LAVA)) {
			if (fluid.getAmount() >= FluidType.BUCKET_VOLUME) {
				return 20000;
			}
		}
		if (fluid.isEmpty() && stack.is(ItemRegistry.WEEDWOOD_BUCKET)) {
			return 300;
		}

		return super.getBurnTime(stack, type);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		var stack = player.getItemInHand(hand);

		if (isMilkBucket(stack)) {
			return ItemUtils.startUsingInstantly(level, player, hand);
		}

		var tank = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if (tank == null) {
			return InteractionResultHolder.fail(stack);
		}

		var pickup = this.tryPickupFluid(stack, level, player);
		if (pickup.getResult() == InteractionResult.SUCCESS) {
			return pickup;
		} else {
			var fluid = stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY);
			if (fluid.getAmount() >= FluidType.BUCKET_VOLUME) {
				return this.tryPlaceFluid(stack, level, player, hand);
			} else {
				return InteractionResultHolder.fail(stack);
			}
		}
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).isEmpty() ? 16 : 1;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		Player player = context.getPlayer();

		if (level.getBlockEntity(pos) instanceof InfuserBlockEntity infuser) {
			if (context.getItemInHand().getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).isEmpty() && infuser.hasInfusion() && infuser.getWaterAmount() >= FluidType.BUCKET_VOLUME) {
				ItemStack infusionBucket = stack.is(ItemRegistry.WEEDWOOD_BUCKET) ? new ItemStack(ItemRegistry.WEEDWOOD_INFUSION_BUCKET.get()) : new ItemStack(ItemRegistry.SYRMORITE_INFUSION_BUCKET.get());
				infusionBucket.set(DataComponentRegistry.INFUSION_BUCKET_DATA, new InfusionBucketData(infuser.getItems().subList(0, InfuserBlockEntity.MAX_INGREDIENTS), infuser.getInfusionTime()));
				infuser.extractFluids(level, pos, new FluidStack(FluidRegistry.SWAMP_WATER_STILL, FluidType.BUCKET_VOLUME));
				ItemStack result = ItemUtils.createFilledResult(stack, player, infusionBucket);
				player.setItemInHand(context.getHand(), result);
				return InteractionResult.SUCCESS;
			}
		}

		if(stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).isEmpty() && context.getClickedFace().getAxis() != Direction.Axis.Y) {
			if(player.mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand())) {
				BlockState blockState = level.getBlockState(pos);

				if(blockState.is(BlockRegistry.RUBBER_LOG) && blockState.getValue(RubberLogBlock.NATURAL)) {
					BlockPos offset = pos.relative(context.getClickedFace());
					BlockState tap = this.tap.defaultBlockState().setValue(RubberTapBlock.FACING, context.getClickedFace());
					if(level.getBlockState(offset).canBeReplaced() && tap.canSurvive(level, offset)) {
						level.setBlockAndUpdate(offset, tap);
						stack.consume(1, player);

						level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1, 1);

						return InteractionResult.sidedSuccess(level.isClientSide());
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Cow cow && !cow.isBaby()) {
			var tank = stack.getCapability(Capabilities.FluidHandler.ITEM);

			if (tank != null && tank.fill(new FluidStack(NeoForgeMod.MILK.get(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE) > 0) {
				player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	// [VanillaCopy] of MilkBucketItem#finishUsingItem
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof ServerPlayer player) {
			CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		if (!level.isClientSide()) {
			entity.removeEffectsCuredBy(EffectCures.MILK);
		}

		if (entity instanceof Player player && !player.getAbilities().instabuild) {
			// BL: instead of shrinking the stack, drain the fluid
			var tank = stack.getCapability(Capabilities.FluidHandler.ITEM);
			if (tank != null) {
				tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
			}
		}

		return stack;
	}

	private InteractionResultHolder<ItemStack> tryPlaceFluid(ItemStack stack, Level level, Player player, InteractionHand hand) {
		if (stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).getAmount() < FluidType.BUCKET_VOLUME)
			return InteractionResultHolder.pass(stack);

		var trace = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
		if (trace.getType() != HitResult.Type.BLOCK)
			return InteractionResultHolder.pass(stack);

		var pos = trace.getBlockPos();
		if (level.mayInteract(player, pos)) {
			var targetPos = pos.relative(trace.getDirection());

			if (player.mayUseItemAt(targetPos, trace.getDirection().getOpposite(), stack)) {
				var result = FluidUtil.tryPlaceFluid(player, level, hand, targetPos, stack, stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).copy().copyWithAmount(FluidType.BUCKET_VOLUME));
				if (result.isSuccess()) {
					ItemStack emptyStack = ItemUtils.createFilledResult(stack, player, result.getResult());
					if (player instanceof ServerPlayer sp) {
						CriteriaTriggers.PLACED_BLOCK.trigger(sp, targetPos, stack);
					}

					player.awardStat(Stats.ITEM_USED.get(this));

					return InteractionResultHolder.sidedSuccess(emptyStack, level.isClientSide());
				}
			}
		}

		return InteractionResultHolder.fail(stack);
	}

	private InteractionResultHolder<ItemStack> tryPickupFluid(ItemStack stack, Level level, Player player) {
		if (!stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).isEmpty())
			return InteractionResultHolder.pass(stack);

		var trace = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (trace.getType() != HitResult.Type.BLOCK)
			return InteractionResultHolder.pass(stack);

		var pos = trace.getBlockPos();
		if (level.mayInteract(player, pos)) {
			var direction = trace.getDirection();
			if (player.mayUseItemAt(pos, direction, stack)) {
				var result = this.tryPickUpFluid(stack, player, level, pos, direction);

				if (result.isSuccess()) {
					ItemStack filledStack = ItemUtils.createFilledResult(stack, player, result.getResult());
					if (!level.isClientSide()) {
						CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, stack);
					}

					return InteractionResultHolder.success(filledStack);
				}
			}
		}

		return InteractionResultHolder.fail(stack);
	}

	private static boolean isMilkBucket(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).is(NeoForgeMod.MILK.get());
	}

	public static boolean isFluidHot(FluidStack stack) {
		return stack.getFluidType().getTemperature() > 473.15F /*200°C*/ || stack.is(Fluids.LAVA);
	}

	// Copy of FluidUtil.tryPickUpFluid, but account for fluid temp before pickup
	private FluidActionResult tryPickUpFluid(ItemStack emptyContainer, @Nullable Player playerIn, Level level, BlockPos pos, Direction side) {
		if (emptyContainer.isEmpty() || level == null || pos == null) {
			return FluidActionResult.FAILURE;
		}

		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		IFluidHandler targetFluidHandler;
		if (block instanceof BucketPickup) {
			targetFluidHandler = new BucketPickupHandlerWrapper(playerIn, (BucketPickup) block, level, pos);
		} else {
			Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, side);
			if (fluidHandler.isEmpty()) {
				return FluidActionResult.FAILURE;
			}
			targetFluidHandler = fluidHandler.get();
		}

		if (!this.allowHotFluids && isFluidHot(targetFluidHandler.getFluidInTank(0))) {
			return FluidActionResult.FAILURE;
		}

		return FluidUtil.tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
	}
}
