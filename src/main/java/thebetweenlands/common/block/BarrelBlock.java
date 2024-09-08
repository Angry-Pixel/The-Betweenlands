package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.BarrelBlockEntity;
import thebetweenlands.common.block.entity.SmokingRackBlockEntity;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.FluidRegistry;

import java.util.Optional;

public class BarrelBlock extends HorizontalBaseEntityBlock {

	private final boolean heatResistant;
	public static final VoxelShape OUTSIDE_SHAPE = Shapes.or(
		Block.box(2.0D, 0.0D, 4.0D, 14.0D, 15.0D, 12.0D),
		Block.box(4.0D, 0.0D, 2.0D, 12.0D, 15.0D, 14.0D));
	public static final VoxelShape INSIDE_SHAPE = Shapes.or(
		Block.box(4.0D, 2.0D, 5.0D, 12.0D, 15.0D, 11.0D),
		Block.box(5.0D, 2.0D, 4.0D, 11.0D, 15.0D, 12.0D));
	public static final VoxelShape SHAPE = Shapes.join(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanOp.ONLY_FIRST);

	public BarrelBlock(boolean heatResistant, Properties properties) {
		super(properties);
		this.heatResistant = heatResistant;
	}

	public boolean isHeatResistant(Level level, BlockPos pos, BlockState state) {
		return this.heatResistant;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, hitResult.getDirection());

		if (fluidHandler.isPresent() && FluidUtil.getFluidHandler(stack).isPresent()) {
			if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel) {
				player.openMenu(barrel, pos);
			}
			return InteractionResult.CONSUME;
		}
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
