package thebetweenlands.common.block.misc;

import java.util.EnumMap;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.IConnectedTextureBlock;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;

public class BlockConnectedPane extends BlockPaneBetweenlands implements IConnectedTextureBlock {

	public static final IUnlistedProperty<Boolean> CULL_TOP = new PropertyBoolUnlisted("cull_top");
	public static final IUnlistedProperty<Boolean> CULL_TOP_NORTH = new PropertyBoolUnlisted("cull_top_north");
	public static final IUnlistedProperty<Boolean> CULL_TOP_EAST = new PropertyBoolUnlisted("cull_top_east");
	public static final IUnlistedProperty<Boolean> CULL_TOP_SOUTH = new PropertyBoolUnlisted("cull_top_south");
	public static final IUnlistedProperty<Boolean> CULL_TOP_WEST = new PropertyBoolUnlisted("cull_top_west");
	public static final EnumMap<EnumFacing, IUnlistedProperty<Boolean>> CULL_TOP_MAP = new EnumMap<>(EnumFacing.class);

	public static final IUnlistedProperty<Boolean> CULL_BOTTOM = new PropertyBoolUnlisted("cull_bottom");
	public static final IUnlistedProperty<Boolean> CULL_BOTTOM_NORTH = new PropertyBoolUnlisted("cull_bottom_north");
	public static final IUnlistedProperty<Boolean> CULL_BOTTOM_EAST = new PropertyBoolUnlisted("cull_bottom_east");
	public static final IUnlistedProperty<Boolean> CULL_BOTTOM_SOUTH = new PropertyBoolUnlisted("cull_bottom_south");
	public static final IUnlistedProperty<Boolean> CULL_BOTTOM_WEST = new PropertyBoolUnlisted("cull_bottom_west");
	public static final EnumMap<EnumFacing, IUnlistedProperty<Boolean>> CULL_BOTTOM_MAP = new EnumMap<>(EnumFacing.class);

	static {
		CULL_TOP_MAP.put(EnumFacing.UP, CULL_TOP);
		CULL_TOP_MAP.put(EnumFacing.NORTH, CULL_TOP_NORTH);
		CULL_TOP_MAP.put(EnumFacing.EAST, CULL_TOP_EAST);
		CULL_TOP_MAP.put(EnumFacing.SOUTH, CULL_TOP_SOUTH);
		CULL_TOP_MAP.put(EnumFacing.WEST, CULL_TOP_WEST);

		CULL_BOTTOM_MAP.put(EnumFacing.DOWN, CULL_BOTTOM);
		CULL_BOTTOM_MAP.put(EnumFacing.NORTH, CULL_BOTTOM_NORTH);
		CULL_BOTTOM_MAP.put(EnumFacing.EAST, CULL_BOTTOM_EAST);
		CULL_BOTTOM_MAP.put(EnumFacing.SOUTH, CULL_BOTTOM_SOUTH);
		CULL_BOTTOM_MAP.put(EnumFacing.WEST, CULL_BOTTOM_WEST);
	}

	public BlockConnectedPane(Material materialIn) {
		super(materialIn);
	}

	public BlockConnectedPane(Material materialIn, boolean canDrop) {
		super(materialIn, canDrop);
	}

	protected boolean canConnectTo(IBlockState state, IBlockAccess world, BlockPos pos, BlockPos toPos, IBlockState toState) {
		return toState.getBlock() == this;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;

		state = this.getCullingState(false, state, world, pos);
		state = this.getCullingState(true, state, world, pos);

		IConnectionRules connectionState = new IConnectionRules() {
			@Override
			public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing face, BlockPos to) {
				return Math.abs(to.getX() - pos.getX() - face.getXOffset()) + Math.abs(to.getY() - pos.getY() - face.getYOffset()) + Math.abs(to.getZ() - pos.getZ() - face.getZOffset()) != 1 && BlockConnectedPane.this.canConnectTo(oldState, world, pos, to, world.getBlockState(to));
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

	protected IExtendedBlockState getCullingState(boolean up, IExtendedBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing facing = up ? EnumFacing.UP : EnumFacing.DOWN;

		EnumMap<EnumFacing, IUnlistedProperty<Boolean>> properties = up ? CULL_TOP_MAP : CULL_BOTTOM_MAP;

		IUnlistedProperty<Boolean> cullPoleProperty = properties.get(facing);

		BlockPos offsetPos = pos.offset(facing);

		boolean render = this.shouldSideBeRendered(state, world, pos, facing);

		if(world.getBlockState(offsetPos).getBlock() == this) {
			state.withProperty(cullPoleProperty, false);

			for(EnumFacing horFacing : EnumFacing.HORIZONTALS) {
				state = state.withProperty(properties.get(horFacing), render && !this.canBeConnectedTo(world, offsetPos, horFacing));
			}
		} else {
			state.withProperty(cullPoleProperty, true);

			for(IUnlistedProperty<Boolean> property : properties.values()) {
				state = state.withProperty(property, render);
			}
		}

		return state;
	}

	@SuppressWarnings("deprecation")
	@Override	
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		BlockPos offsetPos = pos.offset(side);
		IBlockState offsetBlockState = blockAccess.getBlockState(offsetPos);

		if(this.canConnectTo(blockState, blockAccess, pos, offsetPos, offsetBlockState)) {
			if(side.getAxis() != Axis.Y) {
				return false;
			}

			AxisAlignedBB thisAabb = blockState.getBoundingBox(blockAccess, pos);
			AxisAlignedBB otherAabb = offsetBlockState.getBoundingBox(blockAccess, pos.offset(side));

			if(thisAabb.maxX - thisAabb.minX > otherAabb.maxX - otherAabb.minX || thisAabb.maxZ - thisAabb.minZ > otherAabb.maxZ - otherAabb.minZ) {
				return true;
			} else {
				return false;
			}
		}
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { NORTH, EAST, WEST, SOUTH },
				new IUnlistedProperty[] { CULL_TOP, CULL_TOP_NORTH, CULL_TOP_EAST, CULL_TOP_SOUTH, CULL_TOP_WEST, CULL_BOTTOM, CULL_BOTTOM_NORTH, CULL_BOTTOM_EAST, CULL_BOTTOM_SOUTH, CULL_BOTTOM_WEST }));
	}

}
