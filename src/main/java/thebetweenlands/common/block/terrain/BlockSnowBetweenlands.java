package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.EventWinter;

public class BlockSnowBetweenlands extends BlockSnow {
	public BlockSnowBetweenlands() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.1F);
		this.setSoundType(SoundType.SNOW);
		this.setLightOpacity(0);
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!EventWinter.isFroooosty(worldIn) || worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
			int layers = state.getValue(LAYERS);
			if(layers > 1) {
				worldIn.setBlockState(pos, state.withProperty(LAYERS, layers - 1));
			} else {
				worldIn.setBlockToAir(pos);
			}
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos.down());
		Block block = iblockstate.getBlock();

		if (block != BlockRegistry.BLACK_ICE && block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
			BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP);
			return blockfaceshape == BlockFaceShape.SOLID || iblockstate.getBlock().isLeaves(iblockstate, worldIn, pos.down()) || block == this && ((Integer)iblockstate.getValue(LAYERS)).intValue() == 8;
		} else {
			return false;
		}
	}
}
