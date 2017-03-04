package thebetweenlands.common.block.structure;

import java.util.EnumMap;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
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
import thebetweenlands.common.registries.BlockRegistry;

public class BlockThatchRoof extends BlockStairs {
	public static final IUnlistedProperty<Boolean> CORNER_1 = new PropertyBoolUnlisted("corner_1");
	public static final IUnlistedProperty<Boolean> CORNER_2 = new PropertyBoolUnlisted("corner_1");
	public static final IUnlistedProperty<Boolean> CORNER_3 = new PropertyBoolUnlisted("corner_1");
	public static final IUnlistedProperty<Boolean> CORNER_4 = new PropertyBoolUnlisted("corner_1");

	public BlockThatchRoof() {
		super(BlockRegistry.THATCH.getDefaultState());
		this.setHardness(0.5F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), new IProperty<?>[0], new IUnlistedProperty[]{CORNER_1, CORNER_2, CORNER_3, CORNER_4});
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;

		//x, z
		//0, 0
		boolean corner1 = false;
		//1, 0
		boolean corner2 = false;
		//1, 1
		boolean corner3 = false;
		//0, 1
		boolean corner4 = false;

		EnumMap<EnumFacing, EnumFacing> facings = new EnumMap<EnumFacing, EnumFacing>(EnumFacing.class);
		for(EnumFacing side : EnumFacing.VALUES) {
			IBlockState offsetState = worldIn.getBlockState(pos.offset(side));
			if(offsetState.getBlock() instanceof BlockThatchRoof) {
				facings.put(side, offsetState.getValue(FACING));
			}
		}

		switch(state.getValue(FACING)) {
		default:
		case NORTH:
			corner1 = true;
			corner2 = true;
			if(facings.get(EnumFacing.NORTH) == EnumFacing.WEST && facings.get(EnumFacing.EAST) != EnumFacing.NORTH) {
				corner2 = false;
			}
			if(facings.get(EnumFacing.NORTH) == EnumFacing.EAST && facings.get(EnumFacing.WEST) != EnumFacing.NORTH) {
				corner1 = false;
			}
			if(facings.get(EnumFacing.SOUTH) == EnumFacing.WEST && facings.get(EnumFacing.WEST) != EnumFacing.NORTH) {
				corner4 = true;
			}
			if(facings.get(EnumFacing.SOUTH) == EnumFacing.EAST && facings.get(EnumFacing.EAST) != EnumFacing.NORTH) {
				corner3 = true;
			}
			break;
		case SOUTH:
			corner3 = true;
			corner4 = true;
			if(facings.get(EnumFacing.SOUTH) == EnumFacing.WEST && facings.get(EnumFacing.EAST) != EnumFacing.SOUTH) {
				corner3 = false;
			}
			if(facings.get(EnumFacing.SOUTH) == EnumFacing.EAST && facings.get(EnumFacing.WEST) != EnumFacing.SOUTH) {
				corner4 = false;
			}
			if(facings.get(EnumFacing.NORTH) == EnumFacing.WEST && facings.get(EnumFacing.WEST) != EnumFacing.SOUTH) {
				corner1 = true;
			}
			if(facings.get(EnumFacing.NORTH) == EnumFacing.EAST && facings.get(EnumFacing.EAST) != EnumFacing.SOUTH) {
				corner2 = true;
			}
			break;
		case EAST:
			corner2 = true;
			corner3 = true;
			if(facings.get(EnumFacing.EAST) == EnumFacing.SOUTH && facings.get(EnumFacing.NORTH) != EnumFacing.EAST) {
				corner2 = false;
			}
			if(facings.get(EnumFacing.EAST) == EnumFacing.NORTH && facings.get(EnumFacing.SOUTH) != EnumFacing.EAST) {
				corner3 = false;
			}
			if(facings.get(EnumFacing.WEST) == EnumFacing.SOUTH && facings.get(EnumFacing.SOUTH) != EnumFacing.EAST) {
				corner4 = true;
			}
			if(facings.get(EnumFacing.WEST) == EnumFacing.NORTH && facings.get(EnumFacing.NORTH) != EnumFacing.EAST) {
				corner1 = true;
			}
			break;
		case WEST:
			corner4 = true;
			corner1 = true;
			if(facings.get(EnumFacing.WEST) == EnumFacing.SOUTH && facings.get(EnumFacing.NORTH) != EnumFacing.WEST) {
				corner1 = false;
			}
			if(facings.get(EnumFacing.WEST) == EnumFacing.NORTH && facings.get(EnumFacing.SOUTH) != EnumFacing.WEST) {
				corner4 = false;
			}
			if(facings.get(EnumFacing.EAST) == EnumFacing.SOUTH && facings.get(EnumFacing.SOUTH) != EnumFacing.WEST) {
				corner3 = true;
			}
			if(facings.get(EnumFacing.EAST) == EnumFacing.NORTH && facings.get(EnumFacing.NORTH) != EnumFacing.WEST) {
				corner2 = true;
			}
			break;
		}

		return state.withProperty(CORNER_1, corner1).withProperty(CORNER_2, corner2).withProperty(CORNER_3, corner3).withProperty(CORNER_4, corner4);
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
