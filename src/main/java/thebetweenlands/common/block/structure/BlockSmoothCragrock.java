package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockSmoothCragrock extends BlockFalling {
	public BlockSmoothCragrock() {
		super(Material.ROCK);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.STONE);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) { }

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) { }

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) { }

	@Override
	public int tickRate(World worldIn) {
		return 10;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) { }

	@Override
	public void onEndFalling(World world, BlockPos pos, IBlockState fallingState, IBlockState hitState) {
		if(!world.isRemote) {
			world.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), blockSoundType.getStepSound(), SoundCategory.BLOCKS, (blockSoundType.getVolume() + 1.0F) / 2.0F, blockSoundType.getPitch() * 0.8F, false);
			world.playEvent(null, 2001, pos.up(), Block.getIdFromBlock(world.getBlockState(pos).getBlock()));
			world.setBlockToAir(pos);
		}
	}
}
