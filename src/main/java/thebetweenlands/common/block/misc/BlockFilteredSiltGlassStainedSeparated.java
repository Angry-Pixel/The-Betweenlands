package thebetweenlands.common.block.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockFilteredSiltGlassStainedSeparated extends BlockFilteredSiltGlassStained {
    @Override
    public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        return this.getExtendedConnectedTextureState(state, worldIn, pos, new IConnectionRules() {
            @Override
            public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
                EnumFacing.Axis axis = face.getAxis();
                boolean onSamePlane = (axis != EnumFacing.Axis.X || (to.getX() - pos.getX()) == 0) && (axis != EnumFacing.Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != EnumFacing.Axis.Z || (to.getZ() - pos.getZ()) == 0);
                return onSamePlane && world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlassStainedSeparated && world.getBlockState(to).getBlock().getMetaFromState(world.getBlockState(to)) == oldState.getBlock().getMetaFromState(oldState);
            }

            @Override
            public boolean canConnectThrough(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
                EnumFacing.Axis axis = face.getAxis();
                if((axis == EnumFacing.Axis.X && to.getX() - pos.getX() != 0) || (axis == EnumFacing.Axis.Y && to.getY() - pos.getY() != 0) || (axis == EnumFacing.Axis.Z && to.getZ() - pos.getZ() != 0)) {
                    return !world.isSideSolid(to, face.getOpposite(), false) && !(world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlassStainedSeparated);
                }
                return false;
            }
        });
    }
}
