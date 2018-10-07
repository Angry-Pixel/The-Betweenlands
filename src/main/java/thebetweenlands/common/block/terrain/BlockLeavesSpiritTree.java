package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockLeavesSpiritTree extends BlockLeavesBetweenlands {
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);

	public static enum Type {
		TOP, MIDDLE, BOTTOM
	}

	public final Type type;

	public BlockLeavesSpiritTree(Type type) {
		this.type = type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(DECAYABLE, false);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos, world.getBlockState(pos));
	}

	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		switch(this.type) {
		case MIDDLE:
		case BOTTOM:
			IBlockState above = world.getBlockState(pos.up());
			if(this.type == Type.MIDDLE) {
				return above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_TOP || above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE;
			} else {
				return above.getBlock() == BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE;
			}
		default:
			return true;
		}
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(this.type == Type.TOP) {
			super.updateTick(worldIn, pos, state, rand);
		}
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : new Random();
		if(rand.nextInt(140) == 0) {
			drops.add(new ItemStack(ItemRegistry.SPIRIT_FRUIT));
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return new AxisAlignedBB(0.4D, 0.4D, 0.4D, 0.6D, 0.6D, 0.6D);
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(rand.nextInt(100) == 0) {
			double px = (double)pos.getX() + rand.nextDouble() * 0.5D;
			double py = (double)pos.getY() + rand.nextDouble() * 0.5D;
			double pz = (double)pos.getZ() + rand.nextDouble() * 0.5D;
			BLParticles.SPIRIT_BUTTERFLY.spawn(world, px, py, pz);
		}
	}
}