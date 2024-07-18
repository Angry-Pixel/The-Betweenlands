package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.BarrelBlockEntity;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.FluidRegistry;

public class BarrelBlock extends HorizontalBaseEntityBlock {

	private final boolean heatResistant;

	public BarrelBlock(boolean heatResistant, Properties properties) {
		super(properties);
		this.heatResistant = heatResistant;
	}

	public boolean isHeatResistant(Level level, BlockPos pos, BlockState state) {
		return this.heatResistant;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel) {
			if (player.isCrouching()) {
				return ItemInteractionResult.FAIL;
			}

			if (!stack.isEmpty()) {
				IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
				if (handler != null) {
					IItemHandler playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY, null);
					if (playerInventory != null) {
						FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(stack, barrel, playerInventory, Integer.MAX_VALUE, player, !level.isClientSide());

						if (fluidActionResult.isSuccess()) {
							if (!level.isClientSide()) {
								player.setItemInHand(hand, fluidActionResult.getResult());
							}
							return ItemInteractionResult.sidedSuccess(level.isClientSide());
						} else {
							fluidActionResult = FluidUtil.tryFillContainerAndStow(stack, barrel, playerInventory, Integer.MAX_VALUE, player, !level.isClientSide());
							if (fluidActionResult.isSuccess()) {
								if (!level.isClientSide()) {
									player.setItemInHand(hand, fluidActionResult.getResult());
								}
								return ItemInteractionResult.sidedSuccess(level.isClientSide());
							}
						}
					}
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(!level.isClientSide() && level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel) {
			player.openMenu(barrel);
			return InteractionResult.SUCCESS;
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BarrelBlockEntity(pos, state);
	}

	@Override
	public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
		if (level.dimension() == DimensionRegistries.DIMENSION_KEY && level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel) {
			barrel.fill(new FluidStack(FluidRegistry.SWAMP_WATER_STILL, FluidType.BUCKET_VOLUME / 2), IFluidHandler.FluidAction.EXECUTE);
		}
	}
}
