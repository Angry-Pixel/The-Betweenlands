package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockEdgePlant extends BlockSludgeDungeonPlant implements ICustomItemBlock {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    
    protected static final AxisAlignedBB PLANT_AABB_NORTH = new AxisAlignedBB(0D, 0D, 0.5D, 1D, 0.25D, 1D);
    protected static final AxisAlignedBB PLANT_AABB_SOUTH = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.25D, 0.5D);
    protected static final AxisAlignedBB PLANT_AABB_EAST = new AxisAlignedBB(0.0D, 0D, 0D, 0.5D, 0.25D, 1D);
    protected static final AxisAlignedBB PLANT_AABB_WEST = new AxisAlignedBB(0.5D, 0D, 0D, 1D, 0.25D, 1D);
    
    public BlockEdgePlant() {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setSoundType(SoundType.PLANT);
        setHardness(0.1F);
        setCreativeTab(BLCreativeTabs.PLANTS);
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
		case DOWN:
		case UP:
		case NORTH:
			return PLANT_AABB_NORTH;
		case SOUTH:
			return PLANT_AABB_SOUTH;
		case EAST:
			return PLANT_AABB_EAST;
		case WEST:
			return PLANT_AABB_WEST;
		default:
			return PLANT_AABB_NORTH;
		}
	}

	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		boolean hasSupportBlock;
		
		if(state.getBlock().isAir(state, worldIn, pos)) {
			//Block is air during placement
			hasSupportBlock = true;
		} else {
			EnumFacing facing = state.getValue(FACING);
			hasSupportBlock = this.hasSupportBlock(worldIn, pos, facing);
		}
		
		return hasSupportBlock && super.canBlockStay(worldIn, pos, state);
	}
	
	protected boolean hasSupportBlock(World world, BlockPos pos, EnumFacing facing) {
		BlockPos supportPos = pos.offset(facing.getOpposite());
		return world.getBlockState(supportPos).isSideSolid(world, supportPos, facing);
	}
	
	@Override
	public boolean canSpreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		return world.isAirBlock(targetPos) && this.hasSupportBlock(world, targetPos, state.getValue(FACING));
	}
	
	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.getDefaultState().withProperty(FACING, state.getValue(FACING)));
	}
	
	@Override
	public EnumOffsetType getOffsetType() {
		return EnumOffsetType.NONE;
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockEdgePlant(this);
	}
	
	//why does this need a custom item class
	private static class ItemBlockEdgePlant extends ItemBlock {
		private final BlockEdgePlant block;
		
		public ItemBlockEdgePlant(BlockEdgePlant block) {
			super(block);
			this.block = block;
		}
		
		@Override
		public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
			return this.block.canBlockStay(world, pos, newState) && super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		}
	}
}