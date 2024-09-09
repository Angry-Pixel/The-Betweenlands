package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.FluidRegistry;

public class AlgaeBlock extends PlantBlock {

	protected static final VoxelShape PLANT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	public AlgaeBlock(Properties properties) {
		super(PLANT_AABB, false, properties);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return level.getFluidState(pos).is(FluidRegistry.SWAMP_WATER_STILL.get()) && level.getFluidState(pos.above()).is(Fluids.EMPTY);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		entity.makeStuckInBlock(state, new Vec3(0.95F, 1.0F, 0.95F));
	}
}
