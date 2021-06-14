package thebetweenlands.common.block.misc;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.IConnectedTextureBlock;
import thebetweenlands.common.block.IConnectedTextureBlock.IConnectionRules;

public class BlockFilteredSiltGlass extends BlockGlassBetweenlands implements IConnectedTextureBlock {

	public BlockFilteredSiltGlass() {
		super(Material.GLASS);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT_MIPPED;
	}

    @Override
    public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        return this.getExtendedConnectedTextureState(state, worldIn, pos, new IConnectionRules() {
			@Override
			public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
				Axis axis = face.getAxis();
				boolean onSamePlane = (axis != Axis.X || (to.getX() - pos.getX()) == 0) && (axis != Axis.Y || (to.getY() - pos.getY()) == 0) && (axis != Axis.Z || (to.getZ() - pos.getZ()) == 0);
				return onSamePlane && world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlass;
			}

			@Override
			public boolean canConnectThrough(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
				Axis axis = face.getAxis();
				if((axis == Axis.X && to.getX() - pos.getX() != 0) || (axis == Axis.Y && to.getY() - pos.getY() != 0) || (axis == Axis.Z && to.getZ() - pos.getZ() != 0)) {
					return !world.isSideSolid(to, face.getOpposite(), false) && world.getBlockState(to).getBlock() instanceof BlockFilteredSiltGlass == false;
				}
				return false;
			}
		});
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[0]));
    }
}
