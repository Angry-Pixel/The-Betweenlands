package thebetweenlands.common.block.structure;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockDungeonDoorRunes extends BasicBlock implements ITileEntityProvider, IStateMappedBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool INVISIBLE = PropertyBool.create("invisible");

	public BlockDungeonDoorRunes() {
		this(Material.ROCK);
	}

	public BlockDungeonDoorRunes(Material material) {
		super(material);
		setHardness(0.4F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(INVISIBLE, false));
	}
	
	@Nullable
	public static TileEntityDungeonDoorRunes getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityDungeonDoorRunes) {
			return (TileEntityDungeonDoorRunes) tile;
		}
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if(!getStateFromMeta(meta).getValue(INVISIBLE))
			return new TileEntityDungeonDoorRunes();
		return null;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta)).withProperty(INVISIBLE, Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta = meta | ((EnumFacing) state.getValue(FACING)).getIndex();
		if (((Boolean) state.getValue(INVISIBLE)).booleanValue())
			meta |= 8;
		return meta;
	}

	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(INVISIBLE, false);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, INVISIBLE});
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!state.getValue(INVISIBLE)) {
			TileEntityDungeonDoorRunes tile = getTileEntity(world, pos);
			if (tile instanceof TileEntityDungeonDoorRunes) {
				tile.breakAllDoorBlocks(state, state.getValue(FACING), false);
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		IBlockState invisiBlock = getDefaultState().withProperty(INVISIBLE, true);
		if (!state.getValue(INVISIBLE)) {
			if (state.getValue(FACING) == EnumFacing.WEST || state.getValue(FACING) == EnumFacing.EAST) {
				for (int z = -1; z <= 1; z++)
					for (int y = -1; y <= 1; y++)
						if(pos.add(0, y, z) != pos)
							world.setBlockState(pos.add(0, y, z), invisiBlock.withProperty(FACING, state.getValue(FACING)));
			}
			if (state.getValue(FACING) == EnumFacing.NORTH || state.getValue(FACING) == EnumFacing.SOUTH) {
				for (int x = -1; x <= 1; x++)
					for (int y = -1; y <= 1; y++) {
						if(pos.add(x, y, 0) != pos)
							world.setBlockState(pos.add(x, y, 0), invisiBlock.withProperty(FACING, state.getValue(FACING)));
					}
			}
		}
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && !state.getValue(INVISIBLE)) {
			TileEntityDungeonDoorRunes tile = getTileEntity(world, pos);
			if (tile instanceof TileEntityDungeonDoorRunes) {
				TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
				if (facing == state.getValue(FACING)) {
					if(hitY >= 0.0625F && hitY < 0.375F && tileDoor.bottom_rotate == 0) {
						//System.out.println("Bottom Hit");
						tile.cycleBottomState();
					}
					if(hitY >= 0.375F && hitY < 0.625F && tileDoor.mid_rotate == 0) {
						//System.out.println("Mid Hit");
						tile.cycleMidState();
					}
					if(hitY >= 0.625F && hitY <= 0.9375F && tile.top_rotate == 0) {
						//System.out.println("Top Hit");
						tile.cycleTopState();
					}
					world.notifyBlockUpdate(pos, state, state, 3);
					System.out.println("*******");
					System.out.println("T: " + tile.top_state);
					System.out.println("M: "+ tile.mid_state);
					System.out.println("B: "+ tile.bottom_state);
					return true;
				}
			}
		} else 
			return true;
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(new IProperty[] {INVISIBLE}).build();
		
	}
}
