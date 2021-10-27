package thebetweenlands.common.block.misc;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.IConnectedTextureBlock;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;

public class BlockConnectedPane extends BlockPaneBetweenlands implements IConnectedTextureBlock {

	public static final IUnlistedProperty<Integer> CULL_TOP_NORTH = new PropertyIntegerUnlisted("cull_top_north");
	public static final IUnlistedProperty<Integer> CULL_TOP_EAST = new PropertyIntegerUnlisted("cull_top_east");
	public static final IUnlistedProperty<Integer> CULL_TOP_SOUTH = new PropertyIntegerUnlisted("cull_top_south");
	public static final IUnlistedProperty<Integer> CULL_TOP_WEST = new PropertyIntegerUnlisted("cull_top_west");

	public static final IUnlistedProperty<Integer> CULL_BOTTOM_NORTH = new PropertyIntegerUnlisted("cull_bottom_north");
	public static final IUnlistedProperty<Integer> CULL_BOTTOM_EAST = new PropertyIntegerUnlisted("cull_bottom_east");
	public static final IUnlistedProperty<Integer> CULL_BOTTOM_SOUTH = new PropertyIntegerUnlisted("cull_bottom_south");
	public static final IUnlistedProperty<Integer> CULL_BOTTOM_WEST = new PropertyIntegerUnlisted("cull_bottom_west");

	public BlockConnectedPane(Material materialIn) {
		super(materialIn);
	}

	public BlockConnectedPane(Material materialIn, boolean canDrop) {
		super(materialIn, canDrop);
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		
		
		
		IConnectionRules connectionState = new IConnectionRules() {
			@Override
			public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
				return Math.abs(to.getX() - pos.getX() - face.getXOffset()) + Math.abs(to.getY() - pos.getY() - face.getYOffset()) + Math.abs(to.getZ() - pos.getZ() - face.getZOffset()) != 1 && world.getBlockState(to).getBlock() == BlockConnectedPane.this;
			}

			@Override
			public boolean canConnectThrough(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
				Axis axis = face.getAxis();
				if((axis == Axis.X && to.getX() - pos.getX() != 0) || (axis == Axis.Y && to.getY() - pos.getY() != 0) || (axis == Axis.Z && to.getZ() - pos.getZ() != 0)) {
					return true;
				}
				return false;
			}
		};
		return this.getExtendedConnectedTextureState(state, world, pos, connectionState);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { NORTH, EAST, WEST, SOUTH },
				new IUnlistedProperty[] { CULL_TOP_NORTH, CULL_TOP_EAST, CULL_TOP_SOUTH, CULL_TOP_WEST, CULL_BOTTOM_NORTH, CULL_BOTTOM_EAST, CULL_BOTTOM_SOUTH, CULL_BOTTOM_WEST }));
	}

}
