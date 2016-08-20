package thebetweenlands.common.block.terrain;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class BlockRootUnderwater extends BlockSwampWater {
	public BlockRootUnderwater() {
		super(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		ExtendedBlockState state = (ExtendedBlockState) super.createBlockState();
		Collection<IProperty> properties = new ArrayList<IProperty>();
		properties.addAll(state.getProperties());
		Collection<IUnlistedProperty> unlistedProperties = new ArrayList<IUnlistedProperty>();
		unlistedProperties.addAll(state.getUnlistedProperties());
		unlistedProperties.add(BlockRoot.POS_X);
		unlistedProperties.add(BlockRoot.POS_Y);
		unlistedProperties.add(BlockRoot.POS_Z);
		unlistedProperties.add(BlockRoot.NO_BOTTOM);
		unlistedProperties.add(BlockRoot.NO_TOP);
		unlistedProperties.add(BlockRoot.DIST_UP);
		unlistedProperties.add(BlockRoot.DIST_DOWN);
		return new ExtendedBlockState(this, properties.toArray(new IProperty[0]), unlistedProperties.toArray(new IUnlistedProperty[0]));
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
	public BlockRenderLayer getBlockLayer() {
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
}
