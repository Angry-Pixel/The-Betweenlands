package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import thebetweenlands.api.aspect.AspectContainerItem;
import thebetweenlands.common.block.entity.InfuserBlockEntity;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class InfuserBlock extends HorizontalBaseEntityBlock {

	public InfuserBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof InfuserBlockEntity infuser) {

			final Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, null);
			if (fluidHandler.isPresent() && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler.get())) {
				return ItemInteractionResult.SUCCESS;
			}

			if (!player.isCrouching()) {
				if (!stack.isEmpty() && !infuser.hasInfusion()) {
					AspectContainerItem aspectContainer = AspectContainerItem.fromItem(stack, AspectManager.get(level));
					if (aspectContainer.getAspects().size() > 0) {
						ItemStack ingredient = stack;
						for (int i = 0; i < InfuserBlockEntity.MAX_INGREDIENTS; i++) {
							if (infuser.getItem(i).isEmpty()) {
								ItemStack singleIngredient = ingredient.copy();
								singleIngredient.setCount(1);
								infuser.setItem(i, singleIngredient);
								infuser.updateInfusingRecipe();
								if (!player.isCreative())
									singleIngredient.shrink(1);
								level.sendBlockUpdated(pos, state, state, 2);
								if (infuser.getWaterAmount() > 0) {
									level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.3f, 0.9f + level.getRandom().nextFloat() * 0.3f);
								} else {
									level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.3f, 0.9f + level.getRandom().nextFloat() * 0.3f);
								}
								return ItemInteractionResult.SUCCESS;
							}
						}
					}
				}

				if (!stack.isEmpty() && stack.getItem() instanceof LifeCrystalItem) {
					if (infuser.getItem(InfuserBlockEntity.MAX_INGREDIENTS + 1).isEmpty()) {
						infuser.setItem(InfuserBlockEntity.MAX_INGREDIENTS + 1, stack);
						infuser.updateInfusingRecipe();
						if (!player.isCreative()) player.setItemInHand(hand, ItemStack.EMPTY);
					}
					return ItemInteractionResult.SUCCESS;
				}
			}

			if (player.isCrouching() && !infuser.hasInfusion()) {
				for (int i = InfuserBlockEntity.MAX_INGREDIENTS; i >= 0; i--) {
					if (!infuser.getItem(i).isEmpty()) {
						ItemEntity itemEntity = player.drop(infuser.getItem(i).copy(), false);
						if (itemEntity != null) itemEntity.setNoPickUpDelay();
						infuser.setItem(i, ItemStack.EMPTY);
						infuser.updateInfusingRecipe();
						level.sendBlockUpdated(pos, state, state, 2);
						return ItemInteractionResult.SUCCESS;
					}
				}
			}

			if (player.isCrouching()) {
				//TODO data component
//				if (stack.getItem() instanceof ItemBLBucket && ((ItemBLBucket) stack.getItem()).getFluid(stack) == null && infuser.hasInfusion() && infuser.getWaterAmount() >= FluidType.BUCKET_VOLUME) {
//					ItemStack infusionBucket = new ItemStack(ItemRegistry.BL_BUCKET_INFUSION, 1, stack.getMetadata());
//					NBTTagCompound nbtCompound = new NBTTagCompound();
//					infusionBucket.setTagCompound(nbtCompound);
//					nbtCompound.setString("infused", "Infused");
//					NBTTagList nbtList = new NBTTagList();
//					for (int i = 0; i < infuser.getSizeInventory() - 1; i++) {
//						ItemStack stackInSlot = infuser.getStackInSlot(i);
//						if (!stackInSlot.isEmpty()) {
//							nbtList.appendTag(stackInSlot.writeToNBT(new NBTTagCompound()));
//						}
//					}
//					nbtCompound.setTag("ingredients", nbtList);
//					nbtCompound.setInteger("infusionTime", infuser.getInfusionTime());
//					infuser.extractFluids(level, pos, state, new FluidStack(FluidRegistry.SWAMP_WATER_STILL, FluidType.BUCKET_VOLUME));
//					if (stack.getCount() == 1) {
//						player.setItemInHand(hand, infusionBucket.copy());
//						return ItemInteractionResult.SUCCESS;
//					} else {
//						if (!player.getInventory().add(infusionBucket.copy()))
//							player.drop(infusionBucket.copy(), false);
//						stack.shrink(1);
//						return ItemInteractionResult.SUCCESS;
//					}
//				}

				if (!infuser.getItem(InfuserBlockEntity.MAX_INGREDIENTS + 1).isEmpty()) {
					ItemEntity itemEntity = player.drop(infuser.getItem(InfuserBlockEntity.MAX_INGREDIENTS + 1).copy(), false);
					if (itemEntity != null) itemEntity.setPickUpDelay(0);
					infuser.setItem(InfuserBlockEntity.MAX_INGREDIENTS + 1, ItemStack.EMPTY);
					infuser.updateInfusingRecipe();
					level.sendBlockUpdated(pos, state, state, 2);
					return ItemInteractionResult.SUCCESS;
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (player.isCrouching() && level.getBlockEntity(pos) instanceof InfuserBlockEntity infuser && infuser.getStirProgress() >= 90) {
			infuser.setStirProgress(0);
			return InteractionResult.SUCCESS;
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof InfuserBlockEntity infuser) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			if (infuser.getWaterAmount() > 0 && infuser.getTemperature() > 0) {
				int amount = infuser.waterTank.getFluidAmount();
				int capacity = infuser.waterTank.getCapacity();
				float size = 1.0F / capacity * amount;
				float xx = x + 0.5F;
				float yy = y + 0.35F + size * 0.5F;
				float zz = z + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = random.nextFloat() * 0.6F - 0.3F;
				if (random.nextInt((101 - infuser.getTemperature())) / 4 == 0) {
					float[] colors = infuser.currentInfusionColor;
//					BLParticles.BUBBLE_INFUSION.spawn(level, xx + 0.3F - random.nextFloat() * 0.6F, yy, zz + 0.3F - random.nextFloat() * 0.6F, ParticleArgs.get().withScale(0.3F).withColor(colors[0], colors[1], colors[2], 1));
					if (random.nextInt(10) == 0 && infuser.getTemperature() > 70)
						level.playSound(null, xx, yy, zz, SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 1.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.5F);
				}
				if (infuser.getTemperature() >= 100) {
					float[] colors = infuser.currentInfusionColor;
					for (int i = 0; i < 2 + random.nextInt(3); i++) {
//						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F,
//							ParticleArgs.get()
//								.withMotion((random.nextFloat() * 0.25F - 0.125f) * 0.09f, random.nextFloat() * 0.02F + 0.01F, (random.nextFloat() * 0.25F - 0.125f) * 0.09f)
//								.withScale(1f + random.nextFloat() * 2.0F)
//								.withColor(colors[0], colors[1], colors[2], 1)
//								.withData(80, true, 0.01F, true)));
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new InfuserBlockEntity(pos, state);
	}

	@Override
	public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
		if (level.dimension() == DimensionRegistries.DIMENSION_KEY && level.getBlockEntity(pos) instanceof InfuserBlockEntity infuser) {
			infuser.waterTank.fill(new FluidStack(FluidRegistry.SWAMP_WATER_STILL, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
		}
	}
}
