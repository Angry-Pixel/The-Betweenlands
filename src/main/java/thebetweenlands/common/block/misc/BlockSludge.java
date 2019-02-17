package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSludge extends Block {
	private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);

	public BlockSludge() {
		super(BLMaterialRegistry.SLUDGE);
		this.setHardness(0.1F);
		this.setSoundType(SoundType.GROUND);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTranslationKey("thebetweenlands.sludge");
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		return BOUNDS;
	}

	public void generateBlockTemporary(World world, BlockPos pos) {
		world.setBlockState(pos, this.getDefaultState());
		world.scheduleBlockUpdate(pos, this, 20 * 60, 0);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rnd) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity) {
		if (!(entity instanceof IEntityBL) && entity.onGround) {
			entity.setInWeb();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(IBlockState state, IWorldReader blockAccess, BlockPos pos, EnumFacing side) {
		return blockAccess.getBlockState(pos) != this && super.shouldSideBeRendered(state, blockAccess, pos, side);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.SLUDGE_BALL;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
        return false;
    }
	
	@Override
	public boolean isFullCube(IBlockState state) {
        return false;
    }
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.isSideSolid(pos.down(), EnumFacing.UP);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return this.canPlaceBlockAt(world, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if (!this.canPlaceBlockAt(worldIn, pos)) {
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean isSideSolid(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}