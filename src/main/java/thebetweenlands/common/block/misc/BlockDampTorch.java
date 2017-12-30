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
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockDampTorch extends BlockTorch {
	public BlockDampTorch() {
		this.setHardness(0.0F);
		this.setLightLevel(0);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(4) == 0) {
			EnumFacing facing = (EnumFacing)stateIn.getValue(FACING);
			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY() + 0.7D;
			double z = (double)pos.getZ() + 0.5D;

			if (facing.getAxis().isHorizontal()) {
				EnumFacing facingOpposite = facing.getOpposite();
				BLParticles.SMOKE.spawn(worldIn, x + 0.27D * (double)facingOpposite.getFrontOffsetX(), y + 0.22D, z + 0.27D * (double)facingOpposite.getFrontOffsetZ(), ParticleArgs.get().withColor(0.2F, 0.2F, 0.2F, 1.0F));
			} else {
				BLParticles.SMOKE.spawn(worldIn, x, y, z, ParticleArgs.get().withColor(0.2F, 0.2F, 0.2F, 1.0F));
			}
		}
	}
}
