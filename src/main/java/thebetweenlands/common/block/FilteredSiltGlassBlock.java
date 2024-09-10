package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.client.IConnectedTextureBlock;
import thebetweenlands.api.client.IConnectionRules;

public class FilteredSiltGlassBlock extends TransparentBlock implements IConnectedTextureBlock {

	public FilteredSiltGlassBlock(Properties properties) {
		super(properties);
	}

	@Override
	public IConnectionRules createConnectionRules(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		final Block parent = this;
		return new IConnectionRules() {
			@Override
			public boolean canConnectTo(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
				Axis axis = face.getAxis();
				boolean onSamePlane = (axis != Axis.X || (to.getX() - pos.getX()) == 0) && (axis != Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != Axis.Z || (to.getZ() - pos.getZ()) == 0);
				return onSamePlane && world.getBlockState(to).getBlock() == parent;
			}

			@Override
			public boolean canConnectThrough(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
				Axis axis = face.getAxis();
				if((axis == Axis.X && to.getX() - pos.getX() != 0) || (axis == Axis.Y && to.getY() - pos.getY() != 0) || (axis == Axis.Z && to.getZ() - pos.getZ() != 0)) {
					return !IConnectionRules.isSideSolid(world, to, face.getOpposite()) && world.getBlockState(to).getBlock() != parent;
				}
				return false;
			}
		};
	}
	
}
