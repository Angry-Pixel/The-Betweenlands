package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.client.ConnectedTextureBlock;
import thebetweenlands.api.client.ConnectionRules;
import thebetweenlands.common.datagen.tags.BLFluidTagGenerator;
import thebetweenlands.common.registries.BlockRegistry;

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
				// Note: face is the face of this block that is being rendered
				//  e.g. face == UP controls the top face of this block
				
				// Always connect to algae
				if(world.getBlockState(to).is(BlockRegistry.ALGAE)) {
					return true;
				}

				// Check if the block being connected to and the face are on the same plane
				// (E.g. for the UP or DOWN faces, true only if there is no Y difference)
				Axis axis = face.getAxis();
				boolean onSamePlane = (axis != Axis.X || (to.getX() - pos.getX()) == 0) && (axis != Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != Axis.Z || (to.getZ() - pos.getZ()) == 0);
				
				if(!onSamePlane) {
					return false;
				}

				int xDifference = (to.getX() - pos.getX());
				int zDifference = (to.getZ() - pos.getZ());

				// The block below this one, so we check the connection to the block that's inline with the water
				BlockState targetBlock = world.getBlockState(to.below());
				
				// Only connect if the block has a full face connected to the algae
				boolean isSturdy = true;
				if(xDifference != 0) {
					// If xDifference > 0, then check the Negative X face (the face on the target block that points back to this block)
					// If xDifference < 0, then check the Positive X face (the face on the target block that points back to this block)
					Direction direction = Direction.fromDelta(-xDifference, 0, 0);
					isSturdy = isSturdy && targetBlock.isFaceSturdy(level, pos, direction, SupportType.FULL);
				}
				if(zDifference != 0) {
					// If zDifference > 0, then check the Negative Z face (the face on the target block that points back to this block)
					// If zDifference < 0, then check the Positive Z face (the face on the target block that points back to this block)
					Direction direction = Direction.fromDelta(0, 0, -zDifference);
					isSturdy = isSturdy && targetBlock.isFaceSturdy(level, pos, direction, SupportType.FULL);
				}
				
				return isSturdy;
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
