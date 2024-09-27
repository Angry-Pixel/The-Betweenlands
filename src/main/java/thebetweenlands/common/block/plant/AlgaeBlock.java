package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.api.client.ConnectedTextureBlock;
import thebetweenlands.api.client.ConnectionRules;
import thebetweenlands.common.block.farming.DugSoilBlock;
import thebetweenlands.common.datagen.tags.BLFluidTagGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class AlgaeBlock extends PlantBlock implements ConnectedTextureBlock {

	protected static final VoxelShape PLANT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	public AlgaeBlock(Properties properties) {
		super(PLANT_AABB, false, properties);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return level.getFluidState(pos).is(BLFluidTagGenerator.UNDERWATER_PLANT_PLACEABLE) && level.getFluidState(pos.above()).isEmpty();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		entity.makeStuckInBlock(state, new Vec3(0.95F, 1.0F, 0.95F));
	}

	@Override
	public ConnectionRules createConnectionRules(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return new ConnectionRules() {
			@Override
			public boolean canTextureConnectTo(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
				return world.getBlockState(to).is(BlockRegistry.ALGAE);
			}

			@Override
			public boolean canConnectThrough(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
				Direction.Axis axis = face.getAxis();
				//Tries to connect through the block that is next to the connected texture face. This should always be true
				//if the block can't connect to its own faces because otherwise it wouldn't be able to connect to anything
				return (axis == Direction.Axis.X && to.getX() - pos.getX() != 0) || (axis == Direction.Axis.Y && to.getY() - pos.getY() != 0) || (axis == Direction.Axis.Z && to.getZ() - pos.getZ() != 0);
			}
		};
	}

	@Override
	public boolean isFaceConnectedTexture(BlockAndTintGetter level, BlockPos pos, BlockState builder, Direction face) {
		return face.getAxis().isVertical();
	}
}
