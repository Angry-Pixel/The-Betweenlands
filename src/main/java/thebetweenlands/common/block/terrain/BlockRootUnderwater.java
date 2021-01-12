package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockRootUnderwater extends BlockSwampWater {
	public BlockRootUnderwater() {
		super(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHarvestLevel("axe", 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer((ExtendedBlockState) super.createBlockState(), new IProperty[0], new IUnlistedProperty[]{
				BlockRoot.POS_X,
				BlockRoot.POS_Y,
				BlockRoot.POS_Z,
				BlockRoot.NO_BOTTOM,
				BlockRoot.NO_TOP,
				BlockRoot.DIST_UP,
				BlockRoot.DIST_DOWN
		});
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.TANGLED_ROOT;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) super.getExtendedState(oldState, worldIn, pos);

		final int maxLength = 32;
		int distUp = 0;
		int distDown = 0;
		boolean noTop = false;
		boolean noBottom = false;

		IBlockState blockState;
		//Block block;
		for(distUp = 0; distUp < maxLength; distUp++) {
			blockState = worldIn.getBlockState(pos.add(0, 1 + distUp, 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noTop = true;
			break;
		}
		for(distDown = 0; distDown < maxLength; distDown++)
		{
			blockState = worldIn.getBlockState(pos.add(0, -(1 + distDown), 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noBottom = true;
			break;
		}

		return state.withProperty(BlockRoot.POS_X, pos.getX()).withProperty(BlockRoot.POS_Y, pos.getY()).withProperty(BlockRoot.POS_Z, pos.getZ()).withProperty(BlockRoot.DIST_UP, distUp).withProperty(BlockRoot.DIST_DOWN, distDown).withProperty(BlockRoot.NO_TOP, noTop).withProperty(BlockRoot.NO_BOTTOM, noBottom);
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState(), world.isRemote ? 11 : 3);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(BlockRegistry.ROOT);
	}
}
