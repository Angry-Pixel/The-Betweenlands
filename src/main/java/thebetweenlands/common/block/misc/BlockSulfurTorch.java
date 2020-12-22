package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSulfurTorch extends BlockTorch {
	public BlockSulfurTorch() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.WOOD);
		this.setLightLevel(0.9375F);
		this.setTickRandomly(true);
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		if(!worldIn.isRemote && worldIn.isRainingAt(pos)) {
			worldIn.setBlockState(pos, BlockRegistry.SULFUR_TORCH_EXTINGUISHED.getDefaultState().withProperty(FACING, worldIn.getBlockState(pos).getValue(FACING)));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		double px = (double)pos.getX() + 0.5D;
		double py = (double)pos.getY() + 0.7D;
		double pz = (double)pos.getZ() + 0.5D;

		if (enumfacing.getAxis().isHorizontal()) {
			EnumFacing enumfacing1 = enumfacing.getOpposite();
			BLParticles.SULFUR_TORCH.spawn(world, px + 0.27D * (double)enumfacing1.getXOffset(), py + 0.22D, pz + 0.27D * (double)enumfacing1.getZOffset());
		} else {
			BLParticles.SULFUR_TORCH.spawn(world, px, py, pz);
		}
	}
}
