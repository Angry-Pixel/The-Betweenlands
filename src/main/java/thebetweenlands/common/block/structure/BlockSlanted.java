package thebetweenlands.common.block.structure;

import java.util.EnumMap;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;

public class BlockSlanted extends BlockStairs {
	public static final IUnlistedProperty<Boolean> CORNER_NORTH_WEST = new PropertyBoolUnlisted("corner_north_west");
	public static final IUnlistedProperty<Boolean> CORNER_NORTH_EAST = new PropertyBoolUnlisted("corner_north_east");
	public static final IUnlistedProperty<Boolean> CORNER_SOUTH_EAST = new PropertyBoolUnlisted("corner_south_east");
	public static final IUnlistedProperty<Boolean> CORNER_SOUTH_WEST = new PropertyBoolUnlisted("corner_south_west");

	public BlockSlanted(IBlockState modelState) {
		super(modelState);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), new IProperty<?>[0], new IUnlistedProperty[]{CORNER_NORTH_WEST, CORNER_NORTH_EAST, CORNER_SOUTH_EAST, CORNER_SOUTH_WEST});
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;

		//x, z
		//0, 0
		boolean cornerNW = false;
		//1, 0
		boolean cornerNE = false;
		//1, 1
		boolean cornerSE = false;
		//0, 1
		boolean cornerSW = false;

		EnumMap<EnumFacing, EnumHalf> halves = new EnumMap<EnumFacing, EnumHalf>(EnumFacing.class);
		EnumMap<EnumFacing, EnumFacing> facings = new EnumMap<EnumFacing, EnumFacing>(EnumFacing.class);
		for(EnumFacing side : EnumFacing.HORIZONTALS) {
			IBlockState offsetState = worldIn.getBlockState(pos.offset(side));
			if(isBlockStairs(offsetState)) {
				facings.put(side, offsetState.getValue(FACING));
				halves.put(side, offsetState.getValue(HALF));
			}
		}

		EnumHalf half = state.getValue(HALF);

		switch(state.getValue(FACING)) {
		default:
		case NORTH:
			cornerNW = true;
			cornerNE = true;
			if(halves.get(EnumFacing.NORTH) == half && facings.get(EnumFacing.NORTH) == EnumFacing.WEST && facings.get(EnumFacing.EAST) != EnumFacing.NORTH) {
				cornerNE = false;
			}
			if(halves.get(EnumFacing.NORTH) == half && facings.get(EnumFacing.NORTH) == EnumFacing.EAST && facings.get(EnumFacing.WEST) != EnumFacing.NORTH) {
				cornerNW = false;
			}
			if(halves.get(EnumFacing.SOUTH) == half && facings.get(EnumFacing.SOUTH) == EnumFacing.WEST && facings.get(EnumFacing.WEST) != EnumFacing.NORTH) {
				cornerSW = true;
			}
			if(halves.get(EnumFacing.SOUTH) == half && facings.get(EnumFacing.SOUTH) == EnumFacing.EAST && facings.get(EnumFacing.EAST) != EnumFacing.NORTH) {
				cornerSE = true;
			}
			break;
		case SOUTH:
			cornerSE = true;
			cornerSW = true;
			if(halves.get(EnumFacing.SOUTH) == half && facings.get(EnumFacing.SOUTH) == EnumFacing.WEST && facings.get(EnumFacing.EAST) != EnumFacing.SOUTH) {
				cornerSE = false;
			}
			if(halves.get(EnumFacing.SOUTH) == half && facings.get(EnumFacing.SOUTH) == EnumFacing.EAST && facings.get(EnumFacing.WEST) != EnumFacing.SOUTH) {
				cornerSW = false;
			}
			if(halves.get(EnumFacing.NORTH) == half && facings.get(EnumFacing.NORTH) == EnumFacing.WEST && facings.get(EnumFacing.WEST) != EnumFacing.SOUTH) {
				cornerNW = true;
			}
			if(halves.get(EnumFacing.NORTH) == half && facings.get(EnumFacing.NORTH) == EnumFacing.EAST && facings.get(EnumFacing.EAST) != EnumFacing.SOUTH) {
				cornerNE = true;
			}
			break;
		case EAST:
			cornerNE = true;
			cornerSE = true;
			if(halves.get(EnumFacing.EAST) == half && facings.get(EnumFacing.EAST) == EnumFacing.SOUTH && facings.get(EnumFacing.NORTH) != EnumFacing.EAST) {
				cornerNE = false;
			}
			if(halves.get(EnumFacing.EAST) == half && facings.get(EnumFacing.EAST) == EnumFacing.NORTH && facings.get(EnumFacing.SOUTH) != EnumFacing.EAST) {
				cornerSE = false;
			}
			if(halves.get(EnumFacing.WEST) == half && facings.get(EnumFacing.WEST) == EnumFacing.SOUTH && facings.get(EnumFacing.SOUTH) != EnumFacing.EAST) {
				cornerSW = true;
			}
			if(halves.get(EnumFacing.WEST) == half && facings.get(EnumFacing.WEST) == EnumFacing.NORTH && facings.get(EnumFacing.NORTH) != EnumFacing.EAST) {
				cornerNW = true;
			}
			break;
		case WEST:
			cornerSW = true;
			cornerNW = true;
			if(halves.get(EnumFacing.WEST) == half && facings.get(EnumFacing.WEST) == EnumFacing.SOUTH && facings.get(EnumFacing.NORTH) != EnumFacing.WEST) {
				cornerNW = false;
			}
			if(halves.get(EnumFacing.WEST) == half && facings.get(EnumFacing.WEST) == EnumFacing.NORTH && facings.get(EnumFacing.SOUTH) != EnumFacing.WEST) {
				cornerSW = false;
			}
			if(halves.get(EnumFacing.EAST) == half && facings.get(EnumFacing.EAST) == EnumFacing.SOUTH && facings.get(EnumFacing.SOUTH) != EnumFacing.WEST) {
				cornerSE = true;
			}
			if(halves.get(EnumFacing.EAST) == half && facings.get(EnumFacing.EAST) == EnumFacing.NORTH && facings.get(EnumFacing.NORTH) != EnumFacing.WEST) {
				cornerNE = true;
			}
			break;
		}

		return state.withProperty(CORNER_NORTH_WEST, cornerNW).withProperty(CORNER_NORTH_EAST, cornerNE).withProperty(CORNER_SOUTH_EAST, cornerSE).withProperty(CORNER_SOUTH_WEST, cornerSW);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}
