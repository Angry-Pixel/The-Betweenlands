package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;

public class SwampKelpPlantBlock extends GrowingPlantBodyBlock implements LiquidBlockContainer {

	public static final MapCodec<SwampKelpPlantBlock> CODEC = simpleCodec(SwampKelpPlantBlock::new);

	public SwampKelpPlantBlock(BlockBehaviour.Properties properties) {
		super(properties, Direction.UP, Shapes.block(), true);
	}

	@Override
	protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
		return CODEC;
	}

	@Override
	protected GrowingPlantHeadBlock getHeadBlock() {
		return BlockRegistry.SWAMP_KELP.get();
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return FluidRegistry.SWAMP_WATER_STILL.get().getSource(false);
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}
}
