package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockHollowLog extends BlockHorizontal {
	public static final float WALL_THICKNESS = 0.125f;
	
	public static final AxisAlignedBB TOP_BOUNDING_BOX = new AxisAlignedBB(0, 1, 0, 1, 1 - WALL_THICKNESS, 1);
	public static final AxisAlignedBB BOTTOM_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, WALL_THICKNESS, 1);

	public static final AxisAlignedBB SOUTH_BOUNDING_BOX = new AxisAlignedBB(0, 0, 1, 1, 1, 1 - WALL_THICKNESS);
	public static final AxisAlignedBB NORTH_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, WALL_THICKNESS);

	public static final AxisAlignedBB EAST_BOUNDING_BOX = new AxisAlignedBB(1, 0, 0, 1 - WALL_THICKNESS, 1, 1);
	public static final AxisAlignedBB WEST_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, WALL_THICKNESS, 1, 1);
	
	public BlockHollowLog() {
		super(Material.WOOD);
		setHardness(0.8F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return Block.FULL_BLOCK_AABB;
	}

	protected void tryAddCollisionBox(EnumFacing blockFace, IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		
		// dont add collision to faces where they are connected to other logs
		if(blockFace.getAxis() != EnumFacing.Axis.Y && worldIn.isBlockLoaded(pos.offset(facing))) {
			IBlockState otherstate = worldIn.getBlockState(pos.offset(blockFace));
			if(otherstate.getBlock() == BlockRegistry.HOLLOW_LOG && otherstate.getValue(FACING).getAxis() == blockFace.getAxis()) {
				return;
			} 
		}
		
		// not my cleanest code, could do something with an array here?
		AxisAlignedBB box;
		switch(blockFace) {
			case UP:
				box = TOP_BOUNDING_BOX;
				break;
			case DOWN:
				box = BOTTOM_BOUNDING_BOX;
				break;
			case NORTH:
				box = NORTH_BOUNDING_BOX;
				break;
			case SOUTH:
				box = SOUTH_BOUNDING_BOX;
				break;
			case EAST:
				box = EAST_BOUNDING_BOX;
				break;
			case WEST:
				box = WEST_BOUNDING_BOX;
				break;
			default:
				return;
		}
		
		addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
	}
		
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		
		//probably should do something with getActualState() for performance, like stairs do

		EnumFacing facing = state.getValue(FACING);

		// don't do any special checks for top and bottom faces, they're always there
		addCollisionBoxToList(pos, entityBox, collidingBoxes, TOP_BOUNDING_BOX);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOTTOM_BOUNDING_BOX);
		
		// only add collision to sides that should be solid
		switch(facing.getAxis()) {
			case EnumFacing.Axis.Z: // NORTH and SOUTH
				tryAddCollisionBox(EnumFacing.EAST, state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
				tryAddCollisionBox(EnumFacing.WEST, state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
				break;
			case EnumFacing.Axis.X: // EAST and WEST
				tryAddCollisionBox(EnumFacing.NORTH, state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
				tryAddCollisionBox(EnumFacing.SOUTH, state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
				break;
			default:
				// don't do any special collision checks for an invalid log
				addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_BOUNDING_BOX);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_BOUNDING_BOX);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_BOUNDING_BOX);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_BOUNDING_BOX);
				break;
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return side.getDirectionVec().getY() != 0 || 
				blockAccess.getBlockState(pos.offset(side)).getBlock() != this ||
				side.getAxis() != blockAccess.getBlockState(pos.offset(side)).getValue(FACING).getAxis();
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side.getAxis() != base_state.getValue(FACING).getAxis();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this);
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return EnumItemMisc.DRY_BARK.getID();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return face.getAxis() == state.getValue(FACING).getAxis() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }
}
